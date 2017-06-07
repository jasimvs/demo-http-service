package me.jasim.mtrs.core

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture

import scala.concurrent._

/**
  * Created by jsulaiman on 5/15/17.
  */
trait ReservationService {

  def registerShow(imdbId: String, screenId: String, availableSeats: Int)
                  (implicit ec: ExecutionContext): EitherT[Future, String, Show]

  def reserveTicket(imdbId: String, screenId: String)
                   (implicit ec: ExecutionContext): EitherT[Future, String, Show]

  def getShowDetails(imdbId: String, screenId: String)
                    (implicit ec: ExecutionContext): EitherT[Future, String, Show]
}


trait ReservationServiceImpl extends ReservationService {
  this: MovieReservationRepository with ImdbService =>

  override def registerShow(imdbId: String, screenId: String, availableSeats: Int)
                           (implicit ec: ExecutionContext):  EitherT[Future, String, Show] = {

    val titleEither = getImdbTitle(imdbId)
    val existingShowError = getShowDetails(imdbId, screenId)
      .map(_ => "Show already registered.")

    val noExistingShow = existingShowError.swap

    for {
      _ <- noExistingShow
      title <- titleEither
      show <- addShow(Show(imdbId, screenId, title, availableSeats, 0))
    } yield show
  }

  override def reserveTicket(imdbId: String, screenId: String)
                            (implicit ec: ExecutionContext): EitherT[Future, String, Show]  = {

    getShowDetails(imdbId, screenId)
      .flatMap(s => bookTicketForShow(s))
  }

  private def bookTicketForShow(show: Show)
                               (implicit ec: ExecutionContext): EitherT[Future, String, Show] = {
    val remainingSeats = show.availableSeats - show.reservedSeats
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
        _.find(show => show.imdbId == imdbId && show.screenId == screenId)
          .map(show => EitherT.right[Future, String, Show](Future.successful(show)))
          .getOrElse(EitherT.left[Future, String, Show](Future.successful("Show does not exist."))))
}