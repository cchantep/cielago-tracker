package cielago.controllers

import play.api.Play.current
import play.api.mvc.PlainResult
import play.api.test.FakeApplication

import acolyte.{ StatementHandler }

trait ControllerSpec {
  protected def fakeApp(h: Option[StatementHandler] = None): FakeApplication =
    FakeApplication(additionalConfiguration = Map(
      "application.secret" -> "test",
      "evolutionplugin" -> "disabled") ++ h.fold(Map[String, String]())(
        handler ⇒ {
          val id = System.identityHashCode(this).toString
          acolyte.Driver.register(id, handler)

          Map("db.default.driver" -> "acolyte.Driver",
            "db.default.url" -> "jdbc:acolyte:test?handler=%s".format(id))
        }))

}
