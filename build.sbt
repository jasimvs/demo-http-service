organization := "jasim"
name := "rest-test"
version := "0.0.1-SNAPSHOT"
scalaVersion := "2.12.2"

val Http4sVersion = "0.17.0-M2"

libraryDependencies ++= Seq(

  "org.http4s"     %% "http4s-blaze-server" % Http4sVersion,
  "org.http4s"     %% "http4s-circe"        % Http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % Http4sVersion,
  "io.circe"       %% "circe-generic"       % "0.7.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
  "com.typesafe"   %  "config"              % "1.2.1",
  "ch.qos.logback" %  "logback-classic"     % "1.2.1"

)


resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

