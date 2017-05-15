package me.jasim.demo.services

import cats.data._, cats.implicits._

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by jsulaiman on 5/15/17.
  */
trait ImdbService {

  def getImdbTitle(imdbId: String)(implicit ec: ExecutionContext): EitherT[Future, String, String]

}

trait ImdbServiceEmptyImpl extends ImdbService {

  override def getImdbTitle(imdbId: String)(implicit ec: ExecutionContext): EitherT[Future, String, String] =
    EitherT.right[Future, String, String](Future.successful("TODO: fetch title from IMDB"))

}