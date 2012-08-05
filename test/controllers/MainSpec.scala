package cielago.controllers

import org.apache.commons.codec.digest.DigestUtils

import org.specs2.mutable._

import play.api.mvc.{ Cookie, Results, PlainResult }

import play.api.test.{ FakeRequest, FakeApplication, Helpers }

object MainSpec extends Specification {
  "= Main controller specification =".title

  private def fakeApp: FakeApplication =
    FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.apache.derby.jdbc.EmbeddedDriver",
      "db.default.url" -> "jdbc:derby:target/testdb",
      "evolutionplugin" -> "disabled"))

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
          "be unauthorized" in {
            res.header must_== Results.Unauthorized.header
          }
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
        }
      }
    }
  }

  // md5Hex(userName + md5Hex(':' + password))

}
