package cielago.controllers

import org.apache.commons.codec.digest.DigestUtils

import scala.util.{ Failure, Success }

import scala.concurrent.Future

import org.specs2.mutable.Specification

import play.api.Play.current
import play.api.cache.Cache
import play.api.mvc.{ AnyContent, Cookie, Request, Results, SimpleResult }
import play.api.test.{ FakeRequest, Helpers }

import acolyte.Acolyte._
import acolyte.RowLists.rowList2
import acolyte.Rows.row2

import cielago.models.ListInfo

object MainSpec extends Specification with ControllerSpec {
  "Main controller".title

  "Call to index action without cookie/session user digest" should {
    beUnauthorized()
  }

  "Call to index action with invalid user digest as cookie" should {
    beUnauthorized {
      () ⇒ FakeRequest().withCookies(Cookie("userDigest", "invalid"))
    }
  }

  "Call to index action with valid user test1 digest as cookie" should {
    lazy val handler = {
      val noAccount = rowList2(
        classOf[String] -> "list_id",
        classOf[String] -> "account_name")

      Some(handleStatement.
        withQueryDetection("^SELECT").withQueryHandler({ e: acolyte.Execution ⇒
          if (e.parameters(0).value == "31760a4f6e4e5edde51747a52f1a9628")
            (noAccount :+ row2("listId", "test")).asResult
          else noAccount.asResult
        }))

    }

    Helpers.running(fakeApp(handler)) {
      val digest = "userDigest%3A31760a4f6e4e5edde51747a52f1a9628"
      val request = FakeRequest() withCookies {
        Cookie("userDigest", "31760a4f6e4e5edde51747a52f1a9628")
      }
      val plainResult: Future[SimpleResult] = Main.index(request)

      "be authorized" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒
              (res.header.status.
                aka("status") mustNotEqual Results.Unauthorized.header.status).
                and(res.header.headers.get("Set-Cookie") must beSome.which(
                  _ aka "digest" must contain(digest)))
          }
        }

      }
    }
  }

  def beUnauthorized(r: () ⇒ Request[AnyContent] = () ⇒ FakeRequest()) =
    Helpers.running(fakeApp()) {
      val request: Request[AnyContent] = r()
      val plainResult: Future[SimpleResult] = Main.index(request)

      "be unauthorized" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒
              res aka "header" mustEqual Main.NoTrackerAvailable
          }
        }
      }

      "… and not have tracker authorization in cache" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒
              val cookie = res.header.headers.get("Set-Cookie")

              (cookie aka "Set-Cookie" must beNone).
                or(cookie aka "Set-Cookie" must beSome.which(
                  _ aka "digest" must not contain "userDigest%3A"))

          }
        }
      }
    }

}
