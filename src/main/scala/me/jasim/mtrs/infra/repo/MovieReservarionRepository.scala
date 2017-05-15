package me.jasim.mtrs.infra.repo

import cats.data.EitherT
import cats.implicits.catsStdInstancesForFuture
import me.jasim.mtrs.core.{MovieReservationRepository, Show}

import scala.concurrent.{ExecutionContext, Future}



trait MovieReservationInMemoryRepository extends MovieReservationRepository {

  private val shows: scala.collection.mutable.Set[Show] = scala.collection.mutable.Set.empty

  override def getShows(implicit ec: ExecutionContext) =
    EitherT.right[Future, String, Set[Show]](Future.successful(shows.toSet))

  override def addShow(show: Show)(implicit ec: ExecutionContext) =
    if (shows.add(show))
      EitherT.right[Future, String, Show](Future.successful(show))
    else
      EitherT.left[Future, String, Show](Future.successful("Could not add show."))

  override def bookTicket(show: Show)(implicit ec: ExecutionContext) =
    EitherT.right[Future, String, Show](Future.successful({
      val updatedShow = show.copy(reservedSeats = show.reservedSeats + 1)
      shows.remove(show)
      shows.add(updatedShow)
      updatedShow
    }))

}
