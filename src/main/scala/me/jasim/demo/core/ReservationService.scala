package me.jasim.demo.core

import java.util.concurrent.Executors

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.demo.repository.MovieReservationRepository
import me.jasim.demo.services.ImdbService

import scala.concurrent._

/**
  * Created by jsulaiman on 5/15/17.
  */
trait ReservationService extends MovieReservationRepository with ImdbService {

  def registerShow(imdbId: String,
                   screenId: String,
                   availableSeats: Int)(implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show]

  def reserveTicket(imdbId: String, screenId: String): EitherT[Future, _ <: String, _ <: Show]

  def getShowDetails(imdbId: String,
                     screenId: String)(implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show]

}


trait ReservationServiceImpl extends ReservationService {

  override def registerShow(imdbId: String,
                            screenId: String,
                            availableSeats: Int)(implicit ec: ExecutionContext):  EitherT[Future, String, Show] =
    for {
      _ <- getShowDetails(imdbId, screenId)
            .map(_ => "Show already registered.")
            .swap
      title <- getImdbTitle(imdbId)
      show <- addShow(Show(imdbId, screenId, title, availableSeats, 0))
    } yield show

  private val singleThreadEc = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor)

  override def reserveTicket(imdbId: String, screenId: String): EitherT[Future, String, Show]  = {
    implicit val ec: ExecutionContext = singleThreadEc

    getShowDetails(imdbId, screenId)
      .flatMap(s => bookTicketForShow(s))
  }

  private def bookTicketForShow(show: Show)(implicit ec: ExecutionContext): EitherT[Future, String, Show] = {
    val remainingSeats = (show.availableSeats - show.reservedSeats)
    if (remainingSeats > 0) {
      bookTicket(show)
      getShowDetails(show.imdbId, show.screenId)
    } else {
      EitherT.left[Future, String, Show](Future.successful("Tickets not available"))
    }
  }

  override def getShowDetails(imdbId: String, screenId: String)
                             (implicit ec: ExecutionContext): EitherT[Future, String, Show] =
    getShows
      .flatMap(
        _.find(show => (show.imdbId == imdbId && show.screenId == screenId))
          .map(show => EitherT.right[Future, String, Show](Future.successful(show)))
          .getOrElse(EitherT.left[Future, String, Show](Future.successful("Show does not exist."))))
}