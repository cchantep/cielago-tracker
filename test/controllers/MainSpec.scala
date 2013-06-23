package cielago.controllers

import org.apache.commons.codec.digest.DigestUtils

import scala.util.{ Failure, Success }

import scala.concurrent.Future

import scalaz.NonEmptyList
import scalaz.Scalaz._

import org.specs2.mutable.Specification

import play.api.Play.current

import play.api.cache.Cache

import play.api.mvc.{ Cookie, Request, Results, SimpleResult }

import play.api.test.{ FakeRequest, Helpers }

import cielago.models.ListInfo

object MainSpec extends Specification with ControllerSpec {
  "Main controller specification".title

  "Call to index action without cookie/session user digest" should {
    Helpers.running(fakeApp) {
      lazy val request = FakeRequest()
      lazy val plainResult: Future[SimpleResult] = Main.index(request)

      "be unauthorized" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒
              implicit val app = fakeApp

              res.header aka "header" must_== Results.Unauthorized.header
          }
        }
      }

      /*
          "and not have tracker authorization in cache" in {
            res.header.headers.get("Set-Cookie") must be like {
              case Some(digest) ⇒ digest must not contain "userDigest%3A"
            }
          }
          */
    }
  }

  "Call to index action with invalid user digest as cookie" should {
    Helpers.running(fakeApp) {
      lazy val request = FakeRequest() withCookies {
        Cookie("userDigest", "invalid")
      }
      lazy val plainResult: Future[SimpleResult] = Main.index(request)

      "be unauthorized" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒ res.header must_== Results.Unauthorized.header
          }
        }
      }

      /*
          "and not have tracker authorization in cache" in {
            res.header.headers.get("Set-Cookie") must be like {
              case Some(digest) ⇒ digest must not contain "userDigest%3A"
            }
          }
          */
    }
  }

  "Call to index action with valid user test1 digest as cookie" should {
    Helpers.running(fakeApp) {
      implicit val app = current
      val digest = "userDigest%3A31760a4f6e4e5edde51747a52f1a9628"
      lazy val request = FakeRequest() withCookies {
        Cookie("userDigest", "31760a4f6e4e5edde51747a52f1a9628")
      }
      lazy val plainResult: Future[SimpleResult] = Main.index(request)

      "be authorized" in {
        plainResult.value aka "request" must beSome.which { r ⇒
          r aka "result" must beLike {
            case Success(res) ⇒
              (res.header must not be Results.Unauthorized.header).
                and(res.header.headers.get("Set-Cookie") must beSome.which {
                  d ⇒ d aka "digest" must contain(digest)
                })
          }
        }

      }
    }
  }
}
