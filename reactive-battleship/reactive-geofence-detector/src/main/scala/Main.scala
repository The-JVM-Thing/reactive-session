import GeoJsonProtocol._
import akka.actor.ActorSystem
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{Flow, GraphDSL, Source, Zip}
import akka.stream.{ActorMaterializer, FlowShape, OverflowStrategy}
import kamon.Kamon
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import spray.json._

import scala.concurrent.duration._

case class Tick()

object Main extends App {

  Kamon.start()

  import system.dispatcher

  implicit val system = ActorSystem.create("ReactiveGeofenceDetector")

  implicit val materializer = ActorMaterializer()

  val battleships: String = io.Source.fromInputStream(getClass.getResourceAsStream("battleships.json")).mkString

  val concurrentGeofenceDetector = new ConcurrentGeofenceDetector(battleships)

  val conf = system.settings.config
  val kafkaBootstrapServers = conf.getString("kafka.bootstrap-servers")
  val inKafkaTopic = "position_updates"
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId("reactive-geofence-detector")
    .withClientId(s"client-${scala.util.Random.nextInt(100)}")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val generatedPositionsCounter = Kamon.metrics.counter("detected-coordinates")

  val outKafkaTopic = "processed_position_updates"
  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers(kafkaBootstrapServers)

  val slowProcessingSimulator = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    val geofenceDetector = b.add(Flow[Coordinates].map { concurrentGeofenceDetector.process(_)})
    val tick = b.add(Source.tick(0 seconds, 20 millis, Tick()))
    val zip = b.add(Zip[Tuple2[Coordinates, Boolean], Tick]())
    val stripTick = b.add(Flow[Tuple2[Tuple2[Coordinates, Boolean], Tick]].map(_._1))

    geofenceDetector ~> zip.in0
    tick ~> zip.in1

    zip.out ~> stripTick

    FlowShape(geofenceDetector.in, stripTick.out)
  })

  val positionStream = Source.fromGraph(Consumer.committableSource(consumerSettings, Subscriptions.topics(inKafkaTopic)))
    .map { consumerRecord => consumerRecord.committableOffset.commitScaladsl(); consumerRecord }
    .map { consumerRecord => generatedPositionsCounter.increment(); consumerRecord }
    .map(consumerRecord => JsonParser(consumerRecord.record.value()).convertTo[Coordinates])
    .buffer(2000, OverflowStrategy.backpressure)
    .via(slowProcessingSimulator)
    .map(results => JsObject(results._1.toJson.asJsObject.fields + ("hit" -> JsBoolean(results._2))))
    .map(resultsJson => new ProducerRecord[String, String](outKafkaTopic, resultsJson.compactPrint))
    .runWith(Producer.plainSink(producerSettings))

  positionStream.onComplete { x =>
    system.terminate()
  }
}
