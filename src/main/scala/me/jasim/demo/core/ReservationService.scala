package me.jasim.demo.core

import java.util.concurrent.Executors

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.demo.repository.MovieReservationRepository
import me.jasim.demo.services.ImdbServiceEmptyImpl

import scala.concurrent._

/**
  * Created by jsulaiman on 5/15/17.
  */
trait ReservationService {

  def registerShow(imdbId: String, availableSeats: Int, screenId: String)(implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show]

  def reserveTicket(imdbId: String, screenId: String): EitherT[Future, _ <: String, _ <: Show]

  def getShowDetails(imdbId: String, screenId: String)(implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show]

}


trait ReservationServiceInMemoryImpl extends ReservationService with MovieReservationRepository with ImdbServiceEmptyImpl {

  override def registerShow(imdbId: String,
                            availableSeats: Int,
                            screenId: String)(implicit ec: ExecutionContext):  EitherT[Future, String, Show] = {
    EitherT(
      getShowDetails(imdbId, screenId)
        .map(x => EitherT.left(Future.successful("Show already exists")))
        .getOrElse(
          for {
            title: String <- getImdbTitle(imdbId)
            x: Show       <- addShow(Show(imdbId, screenId, title, availableSeats, 0))
            show: Show    <- getShowDetails(imdbId, screenId)
          } yield show)
        .flatMap(_.value))
  }

  private val singleThreadEc = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor)

  override def reserveTicket(imdbId: String, screenId: String): EitherT[Future, String, Show]  = {
    implicit val ec: ExecutionContext = singleThreadEc

    getShowDetails(imdbId, screenId)
      .flatMap(bookTicket)
  }

  private def bookTicket(show: Show)(implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show] = {
    val remainingSeats = (show.availableSeats - show.reservedSeats)
    if (remainingSeats > 0) {
      bookTicket(show)
      getShowDetails(show.imdbId, show.screenId)
    } else {
      EitherT.left(Future.successful("Tickets not available"))
    }
  }

  override def getShowDetails(imdbId: String, screenId: String)
                             (implicit ec: ExecutionContext): EitherT[Future, _ <: String, _ <: Show] =
    getShows
      .flatMap(
        _.find(show => (show.imdbId == imdbId && show.screenId == screenId))
          .map(show => EitherT.right[Future, String, Show](Future.successful(show)))
          .getOrElse(EitherT.left[Future, String, Show](Future.successful("Show does not exist."))))
}