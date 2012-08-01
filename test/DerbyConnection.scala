package cielago

import java.sql.{ Connection, DriverManager }

import scalaz.{ Failure, Success }

trait DerbyConnection extends Cielago {
  def inDerby[A](op: Connection ⇒ A): Valid[A] =
    withConnection(DerbyUtils.getConnection("jdbc:derby:project/testdb"), op)

  private def withConnection[A](c: Connection, op: Connection ⇒ A): Valid[A] =
    try {
      Success(op(c))
    } catch {
      case t: Throwable ⇒ {
        t.printStackTrace()
        Failure(makeFailures(t))
      }
    } finally {
      c.close()
      try {
        DriverManager.getConnection("jdbc:derby:;shutdown=true")
      } catch {
        case t: Throwable ⇒ println("DERBY: " + t.getMessage())
      }
    }
}
