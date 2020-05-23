name := "graphql-sangria-demo"

version := "0.1"

scalaVersion := "2.13.2"

description := "This is just a demo on exploring graphQL possibilities with Sangria"

scalacOptions ++= Seq("-deprecation", "-feature")

mainClass := Some("finalServer.Server")

libraryDependencies ++= Seq(
  "org.sangria-graphql" %% "sangria" % "2.0.0-RC2",
  "org.sangria-graphql" %% "sangria-slowlog" % "2.0.0-M1",
  "org.sangria-graphql" %% "sangria-circe" % "1.3.0",
  "org.sangria-graphql" %% "sangria-spray-json" % "1.0.2",


"com.typesafe.akka" %% "akka-http" % "10.1.12",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.12",
  "de.heikoseeberger" %% "akka-http-circe" % "1.32.0",

  "io.circe" %% "circe-core" % "0.13.0",
  "io.circe" %% "circe-parser" % "0.13.0",
  "io.circe" %% "circe-generic" % "0.13.0",
  "io.circe" %% "circe-optics" % "0.13.0",

  "com.pauldijou" %% "jwt-circe" % "4.3.0",

  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.h2database" % "h2" % "1.4.200",
  "org.slf4j" % "slf4j-nop" % "1.7.30",

  "org.scalatest" %% "scalatest" % "3.1.2",

  "io.spray" %% "spray-json" % "1.3.5"
)
