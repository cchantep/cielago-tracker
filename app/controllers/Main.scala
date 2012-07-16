package cielago.controllers

import java.util.Locale
import java.util.Date

import java.text.SimpleDateFormat

import play.api.mvc.{ Action, Controller, Request }

import cielago.{ Cielago, ListApi, TrackApi }

import cielago.models.{ TrackSelector, TrackPeriodSelector, TrackListSelector, TrackPeriodListSelector }

object Main extends CielagoController with Cielago {

  def index = Action { request ⇒
    val listId: Option[String] = get(request, "listId") flatMap { id ⇒
      id match {
        case "" ⇒ None
        case s  ⇒ Some(s)
      }
    }
    var dateFormat = get(request, "dateFormat")

    println("listId=%s, dateFormat=%s".
      format(listId, dateFormat));

    val period: Option[(Date, Date)] = dateFormat match {
      case None ⇒ None
      case Some(df) ⇒
        getPeriod(request, df).fold({ err ⇒
          println("Fails to parse period: %s" format err)
          None
        },
          o ⇒ o)
    }

    case class Data(
      sel: TrackSelector,
      req: TrackRequest)

    val d: Option[Data] =
      (listId, period) match {
        case (None, None) ⇒ None

        case (None, Some(p)) ⇒
          Some(Data(TrackPeriodSelector(p._1, p._2),
            TrackRequest(Some(p._1), Some(p._2), None)))

        case (Some(id), None) ⇒
          Some(Data(TrackListSelector(id),
            TrackRequest(None, None, Some(id))))

        case (Some(id), Some(p)) ⇒
          Some(Data(TrackPeriodListSelector(p._1, p._2, id),
            TrackRequest(Some(p._1), Some(p._2), Some(id))))
      }

    d match {
      case None ⇒
        Ok(views.html.track(ListApi.all, None, 0, List()))

      case Some(data) ⇒ {
        println("selector=%s" format data.sel)

        Ok(views.html.track(ListApi.all,
          Some(data.req),
          TrackApi.recipientCount(data.sel),
          TrackApi.messageReports(data.sel)))
      }
    }
  }

  private def getPeriod(request: Request[_], format: String): Valid[Option[(Date, Date)]] = unsafe {
    val start = get(request, "startDate")
    val end = get(request, "endDate")

    println("format=%s, start=%s, end=%s".format(format, start, end))

    (start, end) match {
      case (Some(s), Some(e)) ⇒ {
        val lang = get(request, "lang").getOrElse("en")
        val locale = new Locale(lang)
        val fmt = new SimpleDateFormat(format, locale)
        val period = (fmt.parse(s), fmt.parse(e))

        println("period=%s" format period)

        Some(period)
      }

      case _ ⇒ {
        println("x")
        None
      }
    }
  }
}
