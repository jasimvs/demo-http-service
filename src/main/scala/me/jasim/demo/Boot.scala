package me.jasim.demo

import com.typesafe.config.{Config, ConfigFactory}
import org.http4s._
import org.http4s.dsl._
import org.http4s.util.StreamApp
import org.http4s.server.blaze._
import fs2.{Stream, Task}

/**
  * Created by jsulaiman on 5/14/17.
  */
object Boot extends StreamApp {

  val conf = ConfigFactory.load()
  val cl = conf.getConfig("movie-ticket-reservation-system")
  val ip = cl.getString("ip")
  val port = cl.getInt("port")


  val helloWorldService = HttpService {
    case GET -> Root / "hello" =>
      Ok("Hello, better world.")
  }

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(port, ip)
      .mountService(helloWorldService, "/")
      .serve
  }

}
