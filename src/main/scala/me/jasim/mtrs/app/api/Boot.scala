package me.jasim.mtrs.app.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Stream, Task}
import me.jasim.mtrs.core.ReservationServiceImpl
import me.jasim.mtrs.infra.imdb.ImdbServiceEmptyImpl
import me.jasim.mtrs.infra.repo.MovieReservationInMemoryRepository
import org.http4s.server.blaze._
import org.http4s.util.StreamApp

/**
  * Created by jsulaiman on 5/14/17.
  */
object Boot extends StreamApp {

  val conf = Config.loadConfig("application.conf") // if config fails, let it crash

  val movieShowService = new MovieReservationRoutes
                             with ReservationServiceImpl
                             with MovieReservationInMemoryRepository
                             with ImdbServiceEmptyImpl
                             with LazyLogging {}

  override def stream(args: List[String]): Stream[Task, Nothing] = {
    BlazeBuilder
      .bindHttp(conf.port, conf.ip)
      .mountService(movieShowService.movieShowService, "/")
      .mountService(movieShowService.movieReservationService, "/")
      .serve
  }

}
