package cielago

import java.sql.{ Connection, DriverManager }

import scalaz.{ Failure, Success }

import atewaza.DerbyConnector

trait DerbyConnection extends DerbyConnector with Cielago {
  private val jdbcUrl = "jdbc:derby:target/testdb"

  def inDerby[A](op: Connection ⇒ A): Valid[A] =
    super.inDerby(jdbcUrl)(op).
      fold(t ⇒ Failure(stackTraceFailure(t)),
        res ⇒ Success(res))

}
