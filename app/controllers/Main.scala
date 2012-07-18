package cielago.controllers

import java.util.Locale
import java.util.Date

import java.text.SimpleDateFormat

import play.api.Play

import play.api.mvc.{ Action, Controller, Request, Result }

import play.api.data.Form
import play.api.data.Forms.{ date, mapping, nonEmptyText, number, optional, text }

import cielago.{ Cielago, ListApi, TrackApi }

import cielago.models.{ AscendingOrder, DispatchReport, OrderClause, Pagination, TrackSelector, TrackPeriodSelector, TrackListSelector, TrackPeriodListSelector }

object Main extends CielagoController with Cielago {
  private val defaultOrder = Seq(OrderClause("sendTime", AscendingOrder))

  private val trackForm = Form(
    mapping("startDate" -> optional(date("yyyy-MM-dd")),
      "endDate" -> optional(date("yyyy-MM-dd")),
      "listId" -> optional(nonEmptyText),
      "currentPage" -> number)(TrackRequest.apply)(TrackRequest.unapply))

  def index = Action { request ⇒ initialForm }

  def handleForm = Action { implicit request ⇒
    val filledForm = trackForm.bindFromRequest

    filledForm.fold({ errf ⇒
      println("Invalid form data: %s" format errf)

      // @todo low Display error on UI
      Ok(views.html.track(ListApi.all, errf, DispatchReport(0, 0), List()))
    }, tr ⇒ process(filledForm, tr))
  }

  private def process(form: Form[TrackRequest], tr: TrackRequest)(implicit request: Request[_]): Result = {

    /*
    println("start date = %s, end date = %s, list id = %s, page = %s".
      format(tr.startDate, tr.endDate, tr.listId, tr.currentPage))
      */

    request.cookies get "userDigest" map { digest ⇒
      println("user digest = %s" format digest)
    }

    // @todo medium Direct 'match' on case class properties?
    val sel: Option[TrackSelector] =
      (tr.startDate, tr.endDate, tr.listId) match {
        case (None, None, Some(i))    ⇒ Some(TrackListSelector(i))
        case (Some(s), Some(e), None) ⇒ Some(TrackPeriodSelector(s, e))

        case (Some(s), Some(e), Some(i)) ⇒
          Some(TrackPeriodListSelector(s, e, i))

        case _ ⇒ None
      }

    // Sets up pagination
    val pagination = Pagination(10, tr.currentPage, defaultOrder)

    sel match { // @todo medium Fold option?
      case None ⇒ initialForm

      case Some(s) ⇒ {
        Ok(views.html.track(ListApi.all,
          form,
          TrackApi.dispatchReport(s),
          TrackApi.messageReports(s, pagination)))
      }
    }
  }

  private def initialForm: Result =
    Ok(views.html.track(ListApi.all,
      trackForm.fill(TrackRequest(None, None, None, 0)),
      DispatchReport(0, 0),
      List()))

}
