package cielago.controllers

import play.api.mvc.{ Controller, Request }

trait CielagoController extends Controller {

  protected def get(name: String)(implicit request: Request[_]) =
    request.queryString get name flatMap { _.headOption }

}
