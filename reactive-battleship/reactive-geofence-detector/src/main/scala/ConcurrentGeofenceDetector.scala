import com.vividsolutions.jts.geom.{Coordinate, Geometry, GeometryFactory}
import org.geotools.data.simple.SimpleFeatureIterator
import org.geotools.feature.FeatureCollection
import org.geotools.geojson.feature.FeatureJSON
import org.opengis.feature.`type`.FeatureType

import scala.collection.mutable.ListBuffer

object ConcurrentGeofenceDetector {

  val featureCollectionJson = new FeatureJSON()
  val geometryFactory = new GeometryFactory()
}

class ConcurrentGeofenceDetector(geofenceJson: String) {

  val featureCollection = ConcurrentGeofenceDetector.featureCollectionJson.readFeatureCollection(geofenceJson)
  val geometryList = buildGeometryList(featureCollection)

  def buildGeometryList(featureCollection: FeatureCollection[_ <: FeatureType, _ <: org.opengis.feature.Feature]) = {
    val featureIterator: SimpleFeatureIterator = featureCollection.features().asInstanceOf[SimpleFeatureIterator]
    val geometryListBuffer = new ListBuffer[Geometry]()
    while (featureIterator.hasNext) {
      geometryListBuffer += featureIterator.next().getDefaultGeometry.asInstanceOf[Geometry]
    }
    geometryListBuffer.toList
  }

  def process(coordinates: Coordinates) = {

    val point = ConcurrentGeofenceDetector.geometryFactory.createPoint(new Coordinate(coordinates.longitude, coordinates.latitude))
    (coordinates, geometryList.exists(geometry => point.within(geometry)))
  }
}