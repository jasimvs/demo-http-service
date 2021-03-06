import sbt._

val http4sVersion = "0.17.0-M2"
val circeVersion = "0.7.0"

val dependencies =  Seq(
  "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
  "org.http4s"     %% "http4s-circe"        % http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % http4sVersion,
  "org.http4s"     %% "http4s-blaze-client" % http4sVersion,
  "io.circe"       %% "circe-generic"       % circeVersion,
  "io.circe"       %% "circe-parser"        % circeVersion,
  "com.typesafe"   %  "config"              % "1.2.1",
  "ch.qos.logback" %  "logback-classic"     % "1.2.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

  "org.scalatest"  %% "scalatest"           % "3.0.3" % "test"
)

lazy val commonSettings = Seq(
  organization := "jasim",
  name := "rest-test",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.2",
  libraryDependencies := dependencies,
  resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/maven-releases/"
)

lazy val main =
  (project in file("."))
    .settings(commonSettings: _*)
