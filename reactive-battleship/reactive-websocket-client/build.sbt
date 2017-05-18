enablePlugins(JavaAppPackaging)

import com.typesafe.sbt.SbtAspectj._

name := """reactive-websocket-client"""

version := "1.0"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.17"
lazy val akkaHttpVersion = "10.0.6"
lazy val kamonVersion = "0.6.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.15",

  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,

  "io.spray" %% "spray-json" % "1.3.3",
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "io.kamon" %% "kamon-core" % kamonVersion,
  "io.kamon" %% "kamon-statsd" % kamonVersion,
  "io.kamon" %% "kamon-akka-2.4" % kamonVersion,
  "io.kamon" %% "kamon-system-metrics" % kamonVersion,

  "org.aspectj" % "aspectjweaver" % "1.8.9"
)

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