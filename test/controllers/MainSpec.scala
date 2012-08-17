package cielago.controllers

import org.apache.commons.codec.digest.DigestUtils

import scalaz.NonEmptyList
import scalaz.Scalaz._

import org.specs2.mutable._

import play.api.Play.current

import play.api.cache.Cache

import play.api.mvc.{ Cookie, Request, Results, PlainResult }

import play.api.test.{ FakeRequest, Helpers }

import cielago.models.ListInfo

object MainSpec extends Specification with ControllerSpec {
  "= Main controller specification =".title

  "Call to index action without cookie/session user digest" should {
    Helpers.running(fakeApp) {
      lazy val request = FakeRequest()
      lazy val result = Main.index(request)
      lazy val plainResult = result match {
        case res: PlainResult ⇒ Some(res)
        case _                ⇒ None
      }

      "return a plain result" in {
        result must beAnInstanceOf[PlainResult]
      }

      plainResult match {
        case None ⇒ skipped

        case Some(res) ⇒ {
          implicit val app = fakeApp

          "be unauthorized" in {
            res.header must_== Results.Unauthorized.header
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
    }
  }

  "Call to index action with invalid user digest as cookie" should {
    Helpers.running(fakeApp) {
      lazy val request = FakeRequest() withCookies {
        Cookie("userDigest", "invalid")
      }
      lazy val result = Main.index(request)
      lazy val plainResult = result match {
        case res: PlainResult ⇒ Some(res)
        case _                ⇒ None
      }

      "return a plain result" in {
        result must beAnInstanceOf[PlainResult]
      }

      plainResult match {
        case None ⇒ skipped

        case Some(res) ⇒ {
          "be unauthorized" in {
            res.header must_== Results.Unauthorized.header
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
    }
  }

  "Call to index action with valid user test1 digest as cookie" should {
    Helpers.running(fakeApp) {
      implicit val app = current
      lazy val request = FakeRequest() withCookies {
        Cookie("userDigest", "31760a4f6e4e5edde51747a52f1a9628")
      }
      lazy val result = Main.index(request)
      lazy val plainResult = result match {
        case res: PlainResult ⇒ Some(res)
        case _                ⇒ None
      }

      "return a plain result" in {
        result must beAnInstanceOf[PlainResult]
      }

      plainResult match {
        case None ⇒ skipped

        case Some(res) ⇒ {
          "be authorized" in {
            res.header must not be Results.Unauthorized.header
          }

          "and have tracker authorization in cache" in {
            res.header.headers.get("Set-Cookie") must beSome.like {
              case digest ⇒
                digest must contain("userDigest%3A31760a4f6e4e5edde51747a52f1a9628")
            }
          }
        }
      }
    }
  }
}
