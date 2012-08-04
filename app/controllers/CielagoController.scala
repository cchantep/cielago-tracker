package cielago.controllers

import play.api.mvc.{ Action, AnyContent, Controller, Request, Result }

trait CielagoController extends Controller {

  protected def get(name: String)(implicit request: Request[_]) =
    request.queryString get name flatMap { _.headOption }

  def SecureAction(block: (Request[AnyContent]) ⇒ Result): Action[AnyContent] =
    Action { request ⇒
      request.session.get("userDigest") orElse {
        request.cookies.get("userDigest") map { cookie ⇒ cookie.value }
      } match {
        case None         ⇒ Unauthorized
        case Some(digest) ⇒ block(request)
      }
    }
}
