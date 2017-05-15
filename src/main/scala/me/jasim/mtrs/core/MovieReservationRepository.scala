package me.jasim.mtrs.core

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jsulaiman on 5/15/17.
  */
trait MovieReservationRepository {

  def getShows(implicit ec: ExecutionContext): EitherT[Future, String, Set[Show]]

  def addShow(show: Show)(implicit ec: ExecutionContext): EitherT[Future, String, Show]

  def bookTicket(show: Show)(implicit ec: ExecutionContext): EitherT[Future, String, Show]

}
