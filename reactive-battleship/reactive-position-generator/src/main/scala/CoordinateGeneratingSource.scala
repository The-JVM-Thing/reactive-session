import akka.stream.{Attributes, Outlet, SourceShape}
import akka.stream.stage.{GraphStage, GraphStageLogic, OutHandler}

object CoordinateGeneratingSource {
  def apply(latitudeRange: CoordinateRange, longitudeRange: CoordinateRange) = new CoordinateGeneratingSource(latitudeRange, longitudeRange)

}

class CoordinateGeneratingSource(latitudeRange: CoordinateRange, longitudeRange: CoordinateRange) extends GraphStage[SourceShape[Coordinates]] {
  val out: Outlet[Coordinates] = Outlet("CoordinateGeneratingSource")
  override val shape: SourceShape[Coordinates] = SourceShape(out)

  private val r = new scala.util.Random()
  private val latitudeRangeDiff = latitudeRange.max - latitudeRange.min
  private val longitudeRangeDiff = longitudeRange.max - longitudeRange.min

  def generateCoordinates(): Coordinates = {

    val latitude = latitudeRange.min + latitudeRangeDiff * r.nextDouble()
    val longitude = longitudeRange.min + longitudeRangeDiff * r.nextDouble()
    Coordinates(latitude, longitude)
  }

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {

      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          push(out, generateCoordinates())
        }
      })
    }
}