package cielago.controllers

import play.api.Play.current
import play.api.mvc.PlainResult
import play.api.test.FakeApplication

import acolyte.StatementHandler
import acolyte.RowLists.rowList2
import acolyte.Acolyte._

trait ControllerSpec {
  protected def fakeApp(h: Option[StatementHandler] = None): FakeApplication =
    FakeApplication(additionalConfiguration = Map(
      "application.secret" -> "test",
      "evolutionplugin" -> "disabled") ++
      Map("db.default.driver" -> "acolyte.Driver",
        "db.default.url" -> {
          val id = System.identityHashCode(this).toString
          val handler = h.getOrElse(handleStatement.
            withQueryDetection("^SELECT").
            withQueryHandler({ e: acolyte.Execution ⇒
              rowList2(classOf[String] -> "list_id",
                classOf[String] -> "account_name").asResult
            }))

          acolyte.Driver.register(id, handler)

          "jdbc:acolyte:test?handler=%s".format(id)
        }))

}
