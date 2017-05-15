package me.jasim.demo

import org.http4s.util.StreamApp
import org.http4s.server.blaze._
import fs2.{Stream, Task}
import me.jasim.demo.api.MovieReservationRoutes
import me.jasim.demo.core.ReservationServiceImpl
import me.jasim.demo.repository.MovieReservationInMemoryRepository
import me.jasim.demo.services.{ConfigService, ImdbServiceEmptyImpl}

/**
  * Created by jsulaiman on 5/14/17.
  */
object Boot extends StreamApp {

  val conf = ConfigService.loadConfig("application.conf") // if config fails, let it crash

  val movieShowService = new MovieReservationRoutes
                             with ReservationServiceImpl
                             with MovieReservationInMemoryRepository
                             with ImdbServiceEmptyImpl {}

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(conf.port, conf.ip)
      .mountService(movieShowService.movieShowService, "/")
      .mountService(movieShowService.movieReservationService, "/")
      .serve
  }

}
