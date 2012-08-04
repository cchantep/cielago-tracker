package cielago.controllers

import org.specs2.mutable._

import play.api.mvc.{ Results, PlainResult }

import play.api.test.FakeRequest

object MainSpec extends Specification {
  "= Main controller specification =".title

  "Call to index action without cookie" should {
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

