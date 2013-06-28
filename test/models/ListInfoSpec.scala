package cielago.models

import org.specs2.mutable.Specification

object ListInfoSpec extends Specification with ListInfoFixtures {
  "List information" title

  "No tracked list" should {
    "be expected one" in {
      ListInfo.tracked("none")(noCon) aka "tracked" must beNone
    }
  }

  "All information" should {
    "be expected one" in {
      ListInfo.tracked("digest")(allCon) aka "tracked" must beSome.which {
        _.list aka "lists" must haveTheSameElementsAs(all)
      }
    }
  }
}

sealed trait ListInfoFixtures {
  import acolyte.RowLists.rowList2
  import acolyte.Rows.row2
  import acolyte.{ RowList, Row2 }
  import acolyte.Acolyte._

  val all = List(ListInfo("list1", "test1"), ListInfo("list2", "test2"))

  val noRow = rowList2(
    classOf[String] -> "list_id",
    classOf[String] -> "account_name")

  lazy val noCon = connection(handleStatement.
    withQueryDetection("^SELECT ").
    withQueryHandler({ e: acolyte.Execution ⇒ noRow.asResult }))

  lazy val allCon = connection(handleStatement.
    withQueryDetection("^SELECT ").
    withQueryHandler({ e: acolyte.Execution ⇒
      (all.foldLeft(noRow.asInstanceOf[RowList[Row2[String, String]]]) {
        (r, l) ⇒ r :+ row2(l.listId, l.accountName)
      }).asResult
    }))
}
