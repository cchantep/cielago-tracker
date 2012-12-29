package cielago.controllers

import java.util.{ Date, Locale }

import java.text.SimpleDateFormat

import play.api.Play

import play.api.mvc.{ Controller, Request, Result }

import play.api.data.Form
import play.api.data.Forms.{
  date,
  list,
  mapping,
  nonEmptyText,
  number,
  optional,
  text
}

import cielago.{ Cielago, ListApi, TrackApi }

import cielago.models.{
  AscendingOrder,
  DescendingOrder,
  DispatchReport,
  MessageReport,
  OrderClause,
  Paginated,
  Pagination,
  TrackSelector,
  TrackPeriodSelector,
  TrackListSelector,
  TrackPeriodListSelector
}

object Main extends CielagoController with Cielago {
  private val defaultOrder = Seq(OrderClause("sendTime", AscendingOrder))

  private lazy val trackForm = Form(
    mapping("startDate" -> optional(date("yyyy-MM-dd")),
      "endDate" -> optional(date("yyyy-MM-dd")),
      "listId" -> optional(nonEmptyText),
      "order" -> list(nonEmptyText),
      "currentPage" -> number)(TrackRequest.apply)(TrackRequest.unapply))

  val index = SecureAction { request ⇒ initialForm }

  def handleForm = SecureAction { implicit request ⇒
    val filledForm = trackForm.bindFromRequest

    filledForm.fold({ errf ⇒
      println("Invalid form data: %s" format errf)

      // @todo low Display error on UI
      Ok(views.html.track(ListApi.all, errf, DispatchReport(0, 0), Paginated[MessageReport]()))
    }, tr ⇒ process(filledForm, tr))
  }

  private def process(form: Form[TrackRequest], tr: TrackRequest)(implicit request: Request[_]): Result = {

    /*
    println("start date = %s, end date = %s, list id = %s, page = %s".
      format(tr.startDate, tr.endDate, tr.listId, tr.currentPage))
      */

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
    val order = tr.order.foldLeft(Seq[OrderClause]()) {
      (seq, str) ⇒
        str.indexOf(":") match {
          case -1 ⇒ seq
          case i ⇒ seq :+ (str.splitAt(i) match {
            case (c, ":DESC") ⇒ OrderClause(c, DescendingOrder)
            case (c, _)       ⇒ OrderClause(c, AscendingOrder)
          })
        }
    } match {
      case Seq() ⇒ defaultOrder
      case o     ⇒ o
    }

    val pagination = Pagination(10, tr.currentPage, order)

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
      trackForm.fill(TrackRequest()),
      DispatchReport(0, 0),
      Paginated[MessageReport]()))

}
