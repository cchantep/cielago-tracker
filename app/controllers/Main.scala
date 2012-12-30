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
  ListInfo,
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
      "currentPage" -> optional(number))(TrackRequest.apply)(TrackRequest.unapply))

  val index = SecureAction { implicit authenticatedReq ⇒ initialForm }

  val handleForm = SecureAction { implicit request ⇒
    implicit val req = request.data
    val filledForm = trackForm.bindFromRequest

    filledForm.fold({ errf ⇒
      println("Invalid form data: %s" format errf)

      trackResult { trackedLists ⇒
        // @todo low Display error on UI
        Ok(views.html.track(trackedLists,
          errf,
          DispatchReport(0, 0),
          Paginated[MessageReport]()))

      }
    }, tr ⇒ process(filledForm, tr)(request))
  }

  private def process(form: Form[TrackRequest], tr: TrackRequest)(implicit request: Authenticated[Request[_]]): Result = {

    println("start date = %s, end date = %s, list id = %s, page = %s".
      format(tr.startDate, tr.endDate, tr.listId, tr.currentPage))

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

    val pagination = Pagination(10, tr.currentPage getOrElse 0, order)

    sel.fold({ s ⇒
      trackResult { trackedLists ⇒
        Ok(views.html.track(trackedLists,
          form,
          TrackApi.dispatchReport(s),
          TrackApi.messageReports(s, pagination)))
      }
    }, initialForm)

  }

  private def initialForm(implicit req: Authenticated[Request[_]]): Result =
    trackResult { trackedLists ⇒
      Ok(views.html.track(trackedLists,
        trackForm.fill(TrackRequest()),
        DispatchReport(0, 0),
        Paginated[MessageReport]()))
    }

  private def trackResult(serve: List[ListInfo] ⇒ Result)(implicit req: Authenticated[Request[_]]): Result = ListApi.tracked(req.userDigest).
    fold({ trackedLists ⇒ serve(trackedLists.list) }, NoTrackerAvailable)

}
