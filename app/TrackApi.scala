package cielago

import play.api.Play.current

import play.api.db.DB

import models.{ DispatchReport, MessageReport, Paginated, Pagination, TrackSelector }

object TrackApi {

  def dispatchReport(userDigest: String, selector: TrackSelector): DispatchReport = DB withConnection { implicit conn ⇒
    DispatchReport.report(userDigest, selector)
  }

  def messageReports(userDigest: String, selector: TrackSelector, pagination: Pagination): Paginated[MessageReport] =
    DB withConnection { implicit conn ⇒
      MessageReport.find(userDigest, selector, pagination)
    }
}
