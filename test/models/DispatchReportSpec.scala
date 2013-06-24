package cielago.models

import org.specs2.mutable.Specification

object DispatchReportSpec extends Specification
    with DispatchReportClauses with DispatchReportFixtures {

  "Dispatch report" title

  "Failure" should {
    reportExample("occur if there is no row", TrackListSelector("list2")) {
      (_, _, r) ⇒ r must beLeft
    }
  }

  "Report for period" should {
    val start = new java.util.Date()
    val end = new java.util.Date()
    val sel = TrackPeriodSelector(start, end)
    val expected = DispatchReport(2, 3)
    lazy val clause = comparableSql(periodClause.
      replace("{startTime}", "?").
      replace("{endTime}", "?").
      replace("{digest}", "?"))

    reportExample("be exampled one", sel) { (sql, ps, r) ⇒
      (comparableSql(sql) aka "SQL" must endWith(clause)).
        and(ps.size aka "parameter count" mustEqual 3).
        and(ps(0).value aka "start" mustEqual start.getTime).
        and(ps(1).value aka "end" mustEqual end.getTime).
        and(ps(2).value aka "digest" mustEqual "user").
        and(r aka "result" must beRight(expected))
    }
  }

  "Report for list #1" should {
    val sel = TrackListSelector("list1")
    val expected = DispatchReport(1, 2)

    reportExample("be expected one", sel) { (sql, ps, r) ⇒
      (sql aka "SQL" must endWith(listClause.replace("{listId}", "?"))).
        and(ps.size aka "parameter count" mustEqual 1).
        and(ps(0).value aka "list" mustEqual "list1").
        and(r aka "result" must beRight(expected))

    }
  }

  "Report for list and period" should {
    val start = new java.util.Date()
    val end = new java.util.Date()
    val sel = TrackPeriodListSelector(start, end, "list1")
    val expected = DispatchReport(3, 4)

    val clause = comparableSql(periodListClause.
      replace("{startTime}", "?").
      replace("{endTime}", "?").
      replace("{listId}", "?"))

    reportExample("be expected one", sel) { (sql, ps, r) ⇒
      (comparableSql(sql) aka "SQL" must endWith(clause)).
        and(ps.size aka "parameter count" mustEqual 3).
        and(ps(0).value aka "start" mustEqual start.getTime).
        and(ps(1).value aka "end" mustEqual end.getTime).
        and(ps(2).value aka "list" mustEqual "list1").
        and(r aka "result" must beRight(expected))

    }
  }
}

sealed trait DispatchReportFixtures { specs: Specification ⇒
  import org.specs2.text.CodeMarkup
  import org.specs2.specification.Example
  import org.specs2.matcher.MatchResult
  import acolyte.RowLists.rowList2
  import acolyte.Rows.row2
  import acolyte.Acolyte._
  import acolyte.{ ExecutedParameter ⇒ Param }

  def comparableSql(s: String): String = "[ \t]{2,}".r.
    replaceAllIn("[\r\n]+".r.replaceAllIn(s, " "), "")

  val noReport = rowList2(
    classOf[Int] -> "message_count",
    classOf[Int] -> "recipient_count")

  def reportExample(description: String, selector: TrackSelector)(f: (String, List[Param], Either[String, DispatchReport]) ⇒ MatchResult[Any]): Example = {
    var sql: String = null
    var ps: List[Param] = Nil
    val con = connection(handleStatement.withQueryDetection("^SELECT").
      withQueryHandler({ e: acolyte.Execution ⇒
        sql = e.sql; ps = e.parameters

        e.parameters match {
          case Param(s, _) :: Param(e, _) :: Param("list1", _) :: Nil ⇒
            (noReport :+ row2(3, 4)).asResult

          case Param(s, _) :: Param(e, _) :: Param("user", _) :: Nil ⇒
            (noReport :+ row2(2, 3)).asResult

          case Param("list1", _) :: Nil ⇒
            (noReport :+ row2(1, 2)).asResult

          case _ ⇒ noReport.asResult
        }
      }))

    val res: Either[String, DispatchReport] = try {
      Right(DispatchReport.report("user", selector)(con))
    } catch {
      case t: Throwable ⇒ Left(t.getMessage)
    }

    exampleFactory.newExample(CodeMarkup(description), f(sql, ps, res))
  }
}
