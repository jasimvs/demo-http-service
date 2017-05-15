package me.jasim.demo.repository

import cats.data.EitherT, cats.implicits.catsStdInstancesForFuture
import me.jasim.demo.core.Show

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jsulaiman on 5/15/17.
  */
trait MovieReservationRepository {

  def getShows(implicit ec: ExecutionContext): EitherT[Future, String, Set[Show]]

  def addShow(show: Show)(implicit ec: ExecutionContext): EitherT[Future, String, Show]

  def bookTicket(show: Show)(implicit ec: ExecutionContext): EitherT[Future, String, Show]

}

trait MovieReservationRepositoryInMemory extends MovieReservationRepository {

  private val shows: scala.collection.mutable.Set[Show] = scala.collection.mutable.Set.empty

  override def getShows(implicit ec: ExecutionContext) =
    EitherT.right[Future, String, Set[Show]](Future.successful(shows.toSet))

  override def addShow(show: Show)(implicit ec: ExecutionContext) =
    if (shows.add(show))
      EitherT.right[Future, String, Show](Future.successful(show))
    else
      EitherT.left[Future, String, Show](Future.successful("Could not add."))

  override def bookTicket(show: Show)(implicit ec: ExecutionContext) =
    EitherT.right[Future, String, Show](Future.successful({
      val updatedShow = show.copy(reservedSeats = show.reservedSeats + 1)
      shows.update(updatedShow, true)
      updatedShow
    }))

}
