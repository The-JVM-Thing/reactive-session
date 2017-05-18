import akka.actor._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.http.scaladsl.server.Directives.{get, handleWebSocketMessages}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.{ActorMaterializer, FlowShape, Inlet, SourceShape}
import akka.stream.scaladsl.{Flow, GraphDSL, Keep, Sink, Source, ZipWith}
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer

object Main extends App {

  implicit val system = ActorSystem.create("battleship-client")
  implicit val mat = ActorMaterializer()

  val conf = system.settings.config  
  val kafkaBootstrapServers = conf.getString("kafka.bootstrap-servers")
  val inKafkaTopic = "processed_position_updates"
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId("streaming-geofence-detector")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val greeterWebSocketService = Flow.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._

    val consumerFlow = Consumer.plainSource(consumerSettings, Subscriptions.topics(inKafkaTopic))
      .map(consumerRecord => TextMessage(consumerRecord.value()))
    val fromWebsocket = b.add(Flow[Message])
    val zipper = b.add(ZipWith[Message, TextMessage, TextMessage]((request, coordinates) => coordinates))

    fromWebsocket ~> zipper.in0
    consumerFlow ~> zipper.in1

    FlowShape(fromWebsocket.in, zipper.out)
  })

  def route(implicit system: ActorSystem) = get {

      handleWebSocketMessages(greeterWebSocketService)
  }

  Http().bindAndHandle(handler = route(system), interface = "0.0.0.0", port = 8080)
}