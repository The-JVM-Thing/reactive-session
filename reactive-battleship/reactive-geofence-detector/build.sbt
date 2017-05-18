enablePlugins(JavaAppPackaging)

name := """reactive-geofence-detector"""

version := "1.0"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.17"
lazy val kamonVersion = "0.6.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.15",
  "io.spray" %%  "spray-json" % "1.3.3",
  "com.vividsolutions" % "jts" % "1.13",
  "org.geotools" % "gt-geojson" % "11.0",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "io.kamon" %% "kamon-core" % kamonVersion,
  "io.kamon" %% "kamon-statsd" % kamonVersion,
  "io.kamon" %% "kamon-akka-2.4" % kamonVersion,
  "io.kamon" %% "kamon-system-metrics" % kamonVersion,

  "org.aspectj" % "aspectjweaver" % "1.8.9"
)

resolvers += "it.geosolutions" at "http://maven.geo-solutions.it"
resolvers += "opengeo" at "http://repo.opengeo.org"
resolvers += "opengeo2" at "http://download.osgeo.org/webdav/geotools/"

aspectjSettings

javaOptions <++= AspectjKeys.weaverOptions in Aspectj

fork in run := true

assemblyMergeStrategy in assembly := {
  case x if x.endsWith("aop.xml") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

packageName in Docker := packageName.value
version in Docker := version.value