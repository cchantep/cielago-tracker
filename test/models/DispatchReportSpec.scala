package cielago.models

import scalaz.{ Failure, Success }

import org.specs2.mutable._

import cielago.DerbyConnection

object DispatchReportSpec extends Specification with DerbyConnection {

  "Dispatch report for list #1" should {
    val sel = TrackListSelector("list1")
    val expected = Success(DispatchReport(0, 0))

    lazy val report = inDerby { implicit con ⇒ DispatchReport.report(sel) }

    "have expected message and recipient counts" in {
      report must_== expected
    }
  }
}
