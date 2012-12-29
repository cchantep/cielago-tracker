package cielago.controllers

import play.api.Play.current

import play.api.cache.Cache

import play.api.mvc.PlainResult

import play.api.test.FakeApplication

trait ControllerSpec {
  protected def fakeApp: FakeApplication =
    FakeApplication(additionalConfiguration = Map(
      "application.secret" -> "test",
      "db.default.driver" -> "org.apache.derby.jdbc.ClientDriver",
      "db.default.url" -> "jdbc:derby://localhost:1527/target/testdb;deregister=true",
      "evolutionplugin" -> "disabled"),
      classloader = Class.forName("org.apache.derby.jdbc.ClientDriver").getClassLoader)

}
