import spray.json.{DefaultJsonProtocol, JsValue, JsonReader, RootJsonFormat}

case class Feature(featureType: String, coordinates: List[Coordinates])

case class Coordinates(longitude: Double, latitude: Double)

object GeoJsonProtocol extends DefaultJsonProtocol {

  implicit val coordinatesFormat = jsonFormat2(Coordinates)

}
