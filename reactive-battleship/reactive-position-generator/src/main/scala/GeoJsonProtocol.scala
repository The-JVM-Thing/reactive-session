import spray.json.{DefaultJsonProtocol, JsValue, JsonReader, RootJsonFormat}

case class Feature(featureType: String, coordinates: List[CoordinatesArray])

case class CoordinatesArray(longitude: Double, latitude: Double)
case class Coordinates(latitude: Double, longitude: Double)

object GeoJsonProtocol extends DefaultJsonProtocol {

  implicit object CoordinatesArrayFormat extends RootJsonFormat[CoordinatesArray] {
    override def read(json: JsValue): CoordinatesArray = {

      val latlon = json.convertTo[Vector[Double]]
      CoordinatesArray(latlon(0), latlon(1))
    }
    override def write(obj: CoordinatesArray): JsValue = ???
  }
  implicit val coordinatesFormat = jsonFormat2(Coordinates)

  implicit object FeatureFormat extends JsonReader[Feature] {
    override def read(json: JsValue): Feature = {

      Feature(json.asJsObject.fields("type").convertTo[String],
        json.asJsObject.fields("coordinates").convertTo[List[List[CoordinatesArray]]].flatten)
    }
  }
}
