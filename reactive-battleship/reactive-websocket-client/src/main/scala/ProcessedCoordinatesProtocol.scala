import spray.json.DefaultJsonProtocol

case class ProcessedCoordinates(longitude: Double, latitude: Double, hit: Boolean)

object ProcessedCoordinatesProtocol extends DefaultJsonProtocol {

  implicit val processedCoordinatesFormat = jsonFormat3(ProcessedCoordinates)

}
