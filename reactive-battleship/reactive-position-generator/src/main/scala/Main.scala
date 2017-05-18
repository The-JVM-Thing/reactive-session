import GeoJsonProtocol._
import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.{GraphDSL, Source, ZipWith}
import akka.stream.{ActorMaterializer, SourceShape}
import kamon.Kamon
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import spray.json._

import scala.concurrent.duration._

case class Tick()

case class CoordinateRange(min: Double, max: Double)

object Main extends App {

  Kamon.start()

  import system.dispatcher

  implicit val system = ActorSystem.create("StreamingPositionGenerator")
  implicit val materializer = ActorMaterializer()

  val geofence: String = io.Source.fromInputStream(getClass.getResourceAsStream("battleship_arena.json")).mkString
  val geofenceJson = geofence.parseJson.asJsObject
  val coordinates = geofenceJson.convertTo[Feature].coordinates
  val latitudeRange = CoordinateRange(coordinates.minBy(_.latitude).latitude, coordinates.maxBy(_.latitude).latitude)
  val longitudeRange = CoordinateRange(coordinates.minBy(_.longitude).longitude, coordinates.maxBy(_.longitude).longitude)

  val generatedPositionsCounter = Kamon.metrics.counter("generated-coordinates")
  val conf = system.settings.config  
  val kafkaBootstrapServers = conf.getString("kafka.bootstrap-servers")
  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer).withBootstrapServers(kafkaBootstrapServers)
  val kafkaTopic = "position_updates"

  val throttledCoordinateSource = Source.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val zipper = b.add(ZipWith[Tick, Coordinates, Coordinates]((tick, coordinates) => coordinates))

    Source.tick(initialDelay = 0.second, interval = 10.milliseconds, Tick()) ~> zipper.in0
    Source.fromGraph(CoordinateGeneratingSource(latitudeRange, longitudeRange)) ~> zipper.in1
    SourceShape(zipper.out)
  })


  val positionStream = throttledCoordinateSource
    .map(coordinates => new ProducerRecord[String, String](kafkaTopic, coordinates.toJson.compactPrint))
    .map { coordinates => generatedPositionsCounter.increment(); coordinates }
    .runWith(Producer.plainSink(producerSettings))


  positionStream.onComplete { x =>
    system.terminate()
  }
}
