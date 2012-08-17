package cielago.models

import java.sql.{ Connection, DriverManager }

import scalaz.{ Failure, Success }

import atewaza.DerbyConnector

import cielago.Cielago

trait DerbyConnection extends DerbyConnector with Cielago {
  private val jdbcUrl = "jdbc:derby://localhost:1527/target/testdb"

  def inDerby[A](op: Connection ⇒ A): Valid[A] =
    super.inDerby(jdbcUrl)(op).
      fold(t ⇒ Failure(stackTraceFailure(t)),
        res ⇒ Success(res))

}
