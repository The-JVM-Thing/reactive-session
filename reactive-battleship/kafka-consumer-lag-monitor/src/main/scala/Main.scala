import java.nio.ByteBuffer

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import kafka.coordinator.{GroupMetadataManager, OffsetKey}
import kamon.Kamon
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

object Main extends App {

  Kamon.start()

  implicit val system = ActorSystem.create("KafkaConsumerLagMonitor")
  implicit val materializer = ActorMaterializer()

  val conf = system.settings.config
  val kafkaBootstrapServers = conf.getString("kafka.bootstrap-servers")
  val groupToTrack = "reactive-geofence-detector"
  val topicToTrack = "position_updates"
  val consumerOffsetsTopic = "__consumer_offsets"
  val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId("consumer-lag-tracker")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val consumerOffsetsTopicSettings = ConsumerSettings(system, new ByteArrayDeserializer, new ByteArrayDeserializer)
    .withBootstrapServers(kafkaBootstrapServers)
    .withGroupId("consumer-lag-tracker")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  val groupTopicPartitionTrackers = scala.collection.mutable.HashMap[GroupTopicPartition, ActorRef]()

  val consumingStream = Consumer.plainSource(consumerOffsetsTopicSettings, Subscriptions.topics(consumerOffsetsTopic)).map { consumerRecord =>
    Option(consumerRecord.key).map(key => GroupMetadataManager.readMessageKey(ByteBuffer.wrap(key))).flatMap {
      // Only print if the message is an offset record.
      // We ignore the timestamp of the message because GroupMetadataMessage has its own timestamp.
      case offsetKey: OffsetKey =>
        val groupTopicPartition = offsetKey.key
        val value = consumerRecord.value
        val offset =
          if (value == null) -1
          else GroupMetadataManager.readOffsetMessageValue(ByteBuffer.wrap(value)).offset
        Some((groupTopicPartition.group, groupTopicPartition.topicPartition.topic(), groupTopicPartition.topicPartition.partition(), offset))
      case _ => None // no-op
    }
  }
    .filter(x => x.isDefined)
    .map(_.get)
    .filter(offsetMetadata => offsetMetadata._2 == topicToTrack && offsetMetadata._1 == groupToTrack)
    .map(x => (GroupTopicPartition(x._1, x._2, x._3), ConsumedOffset(x._4)))
    .runWith(Sink.foreach(tuple => groupTopicPartitionTrackers.getOrElseUpdate(tuple._1, system.actorOf(Props(classOf[GroupTopicPartitionTracker], tuple._1))) ! tuple._2))

  val producingStream = Consumer.plainSource(consumerSettings, Subscriptions.topics(topicToTrack))
  .map(consumerRecord => (GroupTopicPartition(groupToTrack, topicToTrack, consumerRecord.partition()), ProducedOffset(consumerRecord.offset())))
  .runWith(Sink.foreach(tuple => groupTopicPartitionTrackers.getOrElseUpdate(tuple._1, system.actorOf(Props(classOf[GroupTopicPartitionTracker], tuple._1))) ! tuple._2))
}
