package cielago.models

import scalaz.{ Failure, Success }

import org.specs2.mutable._

import cielago.DerbyConnection

object ListInfoSpec extends Specification with DerbyConnection {

  "List information" should {
    val expected = List(ListInfo("list1", "test1"),
      ListInfo("list2", "test2"))

    lazy val testRes = inDerby { implicit con ⇒ ListInfo.all }

    "be expected instances" in {
      testRes.fold(e ⇒ List(), list ⇒ list) must_== expected
    }
  }
}
