package me.jasim.demo.api

import fs2.{Strategy, Task}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import me.jasim.demo.core.{ReservationService, Show}

import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by jsulaiman on 5/14/17.
  */
trait MovieReservationHttpService extends ReservationService {

  def movieShowService = HttpService {

    case req @ GET -> Root / "show" =>
      Task.fromFuture(getShowDetails("", "").value)(Strategy.fromExecutionContext(global), global)
        .flatMap(z => z match {
          case Left(e) => NotFound(Json.obj("error" -> Json.fromString(e)))
          case Right(r: Show) => Ok(r.asJson)
        })


    case req @ POST -> Root / "show" =>
      for {
        req <- req.as(jsonOf[RegisterShowRequest])
        resp <- Ok(req.asJson)
//        resp <- Ok(registerShow(req.imdbId, req.screenId, req.availableSeats).asJson) // TODO implement this
      } yield resp
  }

  def movieReservationService = HttpService {

    case req @ POST -> Root / "reserve" =>
      for {
        req <- req.as(jsonOf[ReserveTicketRequest])
        resp <- Ok(req.asJson)
//        resp <- Ok(reserveTicket(req.imdbId, req.screenId)) // TODO implement this
      } yield resp
  }

}
