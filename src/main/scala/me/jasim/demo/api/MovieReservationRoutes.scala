package me.jasim.demo.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Strategy, Task}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import me.jasim.demo.core.{ReservationService, Show}

/**
  * Created by jsulaiman on 5/14/17.
  */
trait MovieReservationRoutes extends ReservationService with LazyLogging {

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val strategy = Strategy.fromExecutionContext(ec)

  def movieShowService = HttpService {

    case req @ GET -> Root / "show" => {
      val imdbId = req.params.getOrElse("imdbId", "")
      val screenId = req.params.getOrElse("screenId", "")

      logger.debug(s"Received GET show request with params: IMDB id: $imdbId, Screen ID: $screenId")

      if (imdbId == "" || screenId == "") {
        BadRequest(Json.obj("error" -> Json.fromString("Missing imdbId or screenId")))
      } else {
        Task.fromFuture(getShowDetails(imdbId, screenId).value)
          .flatMap(z => z match {
            case Left(e) => NotFound(Json.obj("error" -> Json.fromString(e)))
            case Right(show: Show) => Ok(show.asJson)
          })
      }
    }

    case req @ POST -> Root / "show" => {
      logger.debug(s"Received POST show request with body: ${req.body}")

      req.as(jsonOf[RegisterShowRequest])
        .flatMap(req => Task.fromFuture(registerShow(req.imdbId, req.screenId, req.availableSeats).value))
        .flatMap(_ match {
          case Right(show: Show)  => Ok(show.asJson)
          case Left(e: String)    => BadRequest(Json.obj("error" -> Json.fromString(e)))
        })
    }
  }

  def movieReservationService = HttpService {

    case req @ POST -> Root / "reserve" => {
      logger.debug(s"Received POST reserve request with body: ${req.body}")

      req.as(jsonOf[ReserveTicketRequest])
        .flatMap(req => Task.fromFuture(reserveTicket(req.imdbId, req.screenId).value))
        .flatMap(_ match {
          case Right(show: Show)  => Ok(show.asJson)
          case Left(e: String)    => BadRequest(Json.obj("error" -> Json.fromString(e)))
        })
    }
  }

}
