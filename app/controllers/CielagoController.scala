package cielago.controllers

import scalaz.NonEmptyList
import scalaz.Scalaz._

import play.api.db.DB

import play.api.Play.current

import play.api.mvc.{
  Action,
  AnyContent,
  Controller,
  PlainResult,
  Request,
  Result
}

import cielago.models.ListInfo

trait CielagoController extends Controller {

  protected def get(name: String)(implicit request: Request[_]) =
    request.queryString get name flatMap { _.headOption }

  def SecureAction(block: (Request[AnyContent]) ⇒ Result): Action[AnyContent] =
    Action { request ⇒
      val userDigest: Option[String] = request.
        session.get("userDigest") orElse {
          request.cookies.get("userDigest") flatMap { cookieDigest ⇒
            DB withConnection { implicit conn ⇒
              ListInfo.tracked(cookieDigest.value) map { nel ⇒
                cookieDigest.value
              }
            }
          }
        }

      userDigest.fold(digest ⇒ {
        block(request) match {
          case pr: PlainResult ⇒ pr withSession {
            request.session + ("userDigest" -> digest)
          }
          case res ⇒ res
        }
      }, Unauthorized)
    }
}
