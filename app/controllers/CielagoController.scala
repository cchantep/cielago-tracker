package cielago.controllers

import play.api.mvc.{ Controller, Request }

trait CielagoController extends Controller {

  protected def get(request: Request[_], name: String) =
    request.queryString get name flatMap { _.headOption }

}
