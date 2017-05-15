package me.jasim.mtrs.app.api

import com.typesafe.scalalogging.LazyLogging
import fs2.{Strategy, Task}
import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import me.jasim.mtrs.core.{ReservationService, Show}

trait MovieReservationRoutes {
  self: ReservationService with LazyLogging =>

  implicit val ec = scala.concurrent.ExecutionContext.Implicits.global
  implicit val strategy = Strategy.fromExecutionContext(ec)

  def movieShowService = HttpService {

    case req @ GET -> Root / "show" =>
      val startTime = System.currentTimeMillis()
      val imdbId = req.params.getOrElse("imdbId", "")
      val screenId = req.params.getOrElse("screenId", "")

      logger.debug(s"Received GET show request with params: IMDB id: $imdbId, Screen ID: $screenId")

      val resp = if (imdbId == "" || screenId == "") {
        BadRequest(Json.obj("error" -> Json.fromString("Missing imdbId or screenId")))
      } else {
        Task.fromFuture(getShowDetails(imdbId, screenId).value)
          .flatMap {
            case Left(e) => NotFound(Json.obj("error" -> Json.fromString(e)))
            case Right(show: Show) => Ok(show.asJson)
          }
      }
      logger.info(s"Processed GET show in ${System.currentTimeMillis() - startTime} ms")
      resp

    case req @ POST -> Root / "show" =>
      val startTime = System.currentTimeMillis()
      logger.debug(s"Received POST show request with body: ${req.body}")

      val resp = req.as(jsonOf[RegisterShowRequest])
        .flatMap(req => Task.fromFuture(registerShow(req.imdbId, req.screenId, req.availableSeats).value))
        .flatMap {
          case Right(show: Show) => Ok(show.asJson)
          case Left(e: String) => BadRequest(Json.obj("error" -> Json.fromString(e)))
        }
      logger.info(s"Processed POST show in ${System.currentTimeMillis() - startTime} ms")
      resp
  }

  def movieReservationService = HttpService {

    case req @ POST -> Root / "reserve" =>
      val startTime = System.currentTimeMillis()
      logger.debug(s"Received POST reserve request with body: ${req.body}")

      val resp = req.as(jsonOf[ReserveTicketRequest])
        .flatMap(req => Task.fromFuture(reserveTicket(req.imdbId, req.screenId).value))
        .flatMap {
          case Right(show: Show) => Ok(show.asJson)
          case Left(e: String) => BadRequest(Json.obj("error" -> Json.fromString(e)))
        }
      logger.info(s"Processed POST reserve in ${System.currentTimeMillis() - startTime} ms")
      resp
  }

}
