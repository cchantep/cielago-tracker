package cielago.models

import scalaz.{ Failure, Success }

import org.specs2.mutable.Specification

object DispatchReportSpec extends Specification {
  "Dispatch report specification" title

  "Dispatch report for list #1" should {
    val sel = TrackListSelector("list1")
    val expected = Success(DispatchReport(0, 0))

    lazy val report = Failure /*
    * TODO: inDerby { implicit con ⇒ DispatchReport.report(sel) }
    */

    "have expected message and recipient counts" in {
      report mustEqual expected
    }
  }
}
