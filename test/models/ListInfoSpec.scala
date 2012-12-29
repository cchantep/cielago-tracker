package cielago.models

import scalaz.{ Failure, Success, Lists }

import org.specs2.mutable.Specification

object ListInfoSpec extends Specification {
  "Specification for model of list information" title

  val all = List(ListInfo("list1", "test1"),
    ListInfo("list2", "test2"))

  "List of all information" should {
    lazy val testRes = List() /*
    * TODO: inDerby { implicit con ⇒ ListInfo.all }
    * */

    "be expected one" in {
      todo
      //testRes.fold(e ⇒ List(), list ⇒ list) must haveTheSameElementsAs(all)
    }
  }

  "No tracked list" should {
    val tracked = List() /*TODO: inDerby { implicit con ⇒
      ListInfo.tracked("invalid")
    }*/

    todo
    /*
    tracked.fold({ f ⇒
      println("failures = %s" format f)
      failure
    }, info ⇒
      "be found for invalid user digest" in { info must beNone })
*/
  }

  "Tracked lists" should {
    /*
    inDerby { implicit con ⇒
      // test1:pass1
      ListInfo.tracked("31760a4f6e4e5edde51747a52f1a9628")
    }.fold(e ⇒ failure, info ⇒
      "only be list1 for user test1" in {
        info must beSome.like {
          case nel ⇒ nel.list must contain(ListInfo("list1", "test1")).only
        }
      })

    inDerby { implicit con ⇒
      // manager:pass_manager
      ListInfo.tracked("ffa220080eaa78453a71929d607b2402")
    }.fold(e ⇒ failure, info ⇒
      "be all lists for manager" in {
        info must beSome.like {
          case nel ⇒ nel.list must haveTheSameElementsAs(all)
        }
      })
      * */

    todo
  }

  // @todo high tracked for Manager
}
