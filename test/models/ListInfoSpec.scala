package cielago.models

import scalaz.{ Failure, Success }

import org.specs2.mutable._

import cielago.DerbyConnection

object ListInfoSpec extends Specification with DerbyConnection {
  "= Specification for model of list information =" title

  "List of all information" should {
    val expected = List(ListInfo("list1", "test1"),
      ListInfo("list2", "test2"))

    lazy val testRes = inDerby { implicit con ⇒ ListInfo.all }

    "be expected one" in {
      testRes.fold(e ⇒ List(), list ⇒ list) must_== expected
    }
  }

  "No tracked list" should {
    inDerby { implicit con ⇒
      ListInfo.tracked("invalid")
    }.fold({ f ⇒
      println("failures = %s" format f)
      failure
    }, info ⇒
      "found for invalid user digest" in { info must_== List[ListInfo]() })
  }

  "Tracked lists" should {
    inDerby { implicit con ⇒
      ListInfo.tracked("31760a4f6e4e5edde51747a52f1a9628")
    }.fold(e ⇒ failure, info ⇒
      "only be list1" in { info must_== List(ListInfo("list1", "test1")) })
  }

  // @todo high tracked for Manager
}
