package cielago

import java.sql.{ Connection, DriverManager }

import scalaz.{ Failure, Success }

trait DerbyConnection extends Cielago {
  def inDerby[A](op: Connection ⇒ A): Valid[A] =
    getConnection("jdbc:derby:target/testdb").fold(err ⇒ Failure(err),
      connection ⇒ withConnection(connection, op))

  private def withConnection[A](c: Connection, op: Connection ⇒ A): Valid[A] =
    try {
      Success(op(c))
    } catch {
      case t: Throwable ⇒ {
        Failure(stackTraceFailure(t))
      }
    } finally {
      c.close()

      try {
        DriverManager.getConnection("jdbc:derby:;shutdown=true")
      } catch {
        case t: Throwable ⇒ println("DERBY: " + t.getMessage())
      }
    }

  private def getConnection(url: String): Valid[Connection] = unsafe {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance()

    DriverManager.getConnection(url)
  }
}
