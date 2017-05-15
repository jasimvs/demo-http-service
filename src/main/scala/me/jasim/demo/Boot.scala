package me.jasim.demo

import org.http4s.util.StreamApp
import org.http4s.server.blaze._
import fs2.{Stream, Task}
import me.jasim.demo.api.MovieReservationHttpService
import me.jasim.demo.core.ReservationServiceInMemoryImpl
import me.jasim.demo.repository.MovieReservationRepositoryInMemory

/**
  * Created by jsulaiman on 5/14/17.
  */
object Boot extends StreamApp {

  val conf = ConfigService.loadConfig("application.conf") // if config fails, let it crash

  val movieShowService = new MovieReservationHttpService
                             with ReservationServiceInMemoryImpl
                             with MovieReservationRepositoryInMemory {}
    .movieShowService

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(conf.port, conf.ip)
      .mountService(movieShowService, "/")
      .serve
  }

}
