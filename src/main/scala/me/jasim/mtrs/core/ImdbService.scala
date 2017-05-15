package me.jasim.mtrs.core

import cats.data.EitherT

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jsulaiman on 5/15/17.
  */
trait ImdbService {

  def getImdbTitle(imdbId: String)(implicit ec: ExecutionContext): EitherT[Future, String, String]

}
