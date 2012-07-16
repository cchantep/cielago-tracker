package cielago

import play.api.Play.current

import play.api.db.DB

import models.{ DispatchReport, MessageReport, TrackSelector }

object TrackApi {

  def recipientCount(selector: TrackSelector): Long =
    DB withConnection { implicit conn ⇒
      DispatchReport.recipientCount(selector)
    }

  def messageReports(selector: TrackSelector): List[MessageReport] =
    DB withConnection { implicit conn ⇒
      MessageReport.find(selector)
    }
}
