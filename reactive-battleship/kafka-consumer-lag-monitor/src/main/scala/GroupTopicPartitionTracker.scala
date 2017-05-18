import akka.actor.Actor
import kamon.Kamon
import scala.concurrent.duration._

case class GroupTopicPartition(group: String, topic: String, partition: Int)
case class ProducedOffset(producedOffset: Long)
case class ConsumedOffset(consumedOffset: Long)
case class PostMetric()

class GroupTopicPartitionTracker(gtp: GroupTopicPartition) extends Actor {

  var producedOffset = 0l
  var consumedOffset = 0l
  val metric = Kamon.metrics.gauge("lag", Map("group" -> gtp.group, "partition" -> gtp.partition.toString))(0l)

  import context.dispatcher
  val tick =
    context.system.scheduler.schedule(1 second, 1 second, self, PostMetric)

  override def receive: Receive = {
    case x: ProducedOffset =>
      producedOffset = x.producedOffset
    case x: ConsumedOffset =>
      consumedOffset = x.consumedOffset
    case PostMetric =>
      val lag = producedOffset - consumedOffset
      metric.record(if (lag > 0) lag else 0)
  }
}
