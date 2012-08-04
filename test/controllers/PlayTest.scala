package cielago.controllers

import play.api.Application

import play.api.test.FakeApplication

trait PlayTest {
  def withApplication[A](block: Application ⇒ A): A =
    block(FakeApplication())

}
