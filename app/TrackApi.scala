package cielago

import play.api.Play.current

import play.api.db.DB

import models.{ DispatchReport, MessageReport, Pagination, TrackSelector }

object TrackApi {

  def dispatchReport(selector: TrackSelector): DispatchReport =
    DB withConnection { implicit conn ⇒
      DispatchReport.report(selector)
    }

  def messageReports(selector: TrackSelector, pagination: Pagination): List[MessageReport] =
    DB withConnection { implicit conn ⇒
      MessageReport.find(selector, pagination)
    }
}
