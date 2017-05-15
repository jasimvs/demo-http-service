package me.jasim.mtrs.infra.imdb

import cats.data._
import cats.implicits._
import me.jasim.mtrs.core.ImdbService

import scala.concurrent.{ExecutionContext, Future}

trait ImdbServiceEmptyImpl extends ImdbService {

  override def getImdbTitle(imdbId: String)(implicit ec: ExecutionContext): EitherT[Future, String, String] =
    EitherT.right[Future, String, String](Future.successful("TODO: fetch title from IMDB"))

}