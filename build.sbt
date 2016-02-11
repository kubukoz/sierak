name := "sierak"

version := "1.0"

scalaVersion := "2.11.7"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

val akkaV = "2.4.1"
val akkaStreamV = "2.0-M2"
val scalaTestV = "2.2.5"

enablePlugins(JavaAppPackaging)

resolvers += "softprops-maven" at "http://dl.bintray.com/content/softprops/maven"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.4.3",
  "com.typesafe.play" % "play-ws_2.11" % "2.4.3",
  "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1",
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
  "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaStreamV,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV,
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % akkaStreamV,
  "org.scalatest" %% "scalatest" % scalaTestV % "test",
  "me.lessis" %% "courier" % "0.1.3"
)

herokuAppName in Compile := "sierak"