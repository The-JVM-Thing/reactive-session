enablePlugins(JavaAppPackaging)

name := """kafka-consumer-lag-monitor"""

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
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "io.kamon" %% "kamon-core" % kamonVersion,
  "io.kamon" %% "kamon-statsd" % kamonVersion,

  "org.apache.kafka" %% "kafka" % "0.10.2.0"
)

assemblyMergeStrategy in assembly := {
  case x if x.endsWith("aop.xml") => MergeStrategy.first
  case x if x.contains("org/slf4j/impl") => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

packageName in Docker := packageName.value
version in Docker := version.value
