import me.jasim.mtrs.app.api.Boot
import me.jasim.mtrs.core.Show
import io.circe._
import io.circe.parser.decode
import io.circe.generic.auto._
import org.scalatest.{Matchers, WordSpec}
import org.http4s._
import org.http4s.dsl._
import org.http4s.client._
import org.http4s.client.blaze.{defaultClient => client}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by jsulaiman on 5/16/17.
  */
class IntegrationTestsSpec extends WordSpec with Matchers {

  val startApp = Future(Boot.main(Array.empty))

  val reserveReq1: String = "{\"imdbId\":\"tt555112\", \"screenId\":\"screen1\"}"
  val registerShowReq1: String = "{\"imdbId\":\"tt555112\", \"availableSeats\":5, \"screenId\":\"screen1\"}"
  val registeredShow = Show("tt555112", "screen1", "TODO: fetch title from IMDB", 5, 0)

  val registeredShowAfter1Booking = Show("tt555112", "screen1", "TODO: fetch title from IMDB", 5, 1)

  val reserveReq2_diffScreen: String = "{\"imdbId\":\"tt555112\", \"screenId\":\"screen2\"}"
  val reserveReq3_diffMovie: String = "{\"imdbId\":\"tt555113\", \"screenId\":\"screen1\"}"

  "Get show details" when {
    " show is not registered" should {
      " not get details." in {

        val req = GET(uri("http://127.0.0.1:8080/show?imdbId=tt555112&screenId=screen1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == NotFound
          case _ => false
        })
      }
    }
  }

  val header = Header("Content-Type", "application/json")
  "Reserve a ticket" when {
    " the show is not registered " should {
      " not reserve a ticket ." in {

        val req = POST(uri("http://127.0.0.1:8080/reserve"))
          .putHeaders(header)
          .withBody(reserveReq1)

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == BadRequest
          case _ => false
        })
      }
    }
  }

  "Register a show" when {
    " the show is not registered " should {
      " register the show." in {

        val req = POST(uri("http://127.0.0.1:8080/show"))
          .putHeaders(header)
          .withBody(registerShowReq1)

        val exp = registeredShow

        val response = client.expect[String](req)

        assert(response.unsafeAttemptRun() match {
          case Right(response) => { decode[Show](response) == Right[Error, Show](exp)}
          case Left(er) => false
        })
      }
    }
  }


  "Register a show" when {
    " the show is registered " should {
      " not register the show." in {

        val req = POST(uri("http://127.0.0.1:8080/show"))
          .putHeaders(header)
          .withBody(registerShowReq1)

        val response = client.expect[String](req)

        assert(response.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == BadRequest
          case _ => false
        })
      }
    }
  }

  "Get show details" when {
    " show is registered" should {
      " get details." in {

        val req = GET(uri("http://127.0.0.1:8080/show?imdbId=tt555112&screenId=screen1"))

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Right(response) => { decode[Show](response) == Right[Error, Show](registeredShow)}
          case Left(er) => false
        })
      }
    }
  }

  "Reserve a ticket" when {
    " the show is registered " should {
      " reserve a ticket ." in {

        val req = POST(uri("http://127.0.0.1:8080/reserve"))
          .putHeaders(header)
          .withBody(reserveReq1)

        val response = client.expect[String](req)

        assert(response.unsafeAttemptRun() match {
          case Right(response) => { decode[Show](response) == Right[Error, Show](registeredShowAfter1Booking)}
          case Left(er) => false
        })
      }
    }
  }

  "Reserve a ticket" when {
    " movie is registered, screen is not " should {
      " not reserve a ticket ." in {

        val req = POST(uri("http://127.0.0.1:8080/reserve"))
          .putHeaders(header)
          .withBody(reserveReq2_diffScreen)

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == BadRequest
          case _ => false
        })
      }
    }
  }

  "Reserve a ticket" when {
    " movie is not registered, screen is registered " should {
      " not reserve a ticket ." in {

        val req = POST(uri("http://127.0.0.1:8080/reserve"))
          .putHeaders(header)
          .withBody(reserveReq3_diffMovie)

        val responseBody = client.expect[String](req)

        assert(responseBody.unsafeAttemptRun() match {
          case Left(er: UnexpectedStatus) => er.status == BadRequest
          case _ => false
        })
      }
    }
  }

}
