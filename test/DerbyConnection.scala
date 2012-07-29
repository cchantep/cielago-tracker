package cielago.test

import java.sql.{ Connection, DriverManager }

sealed trait DerbyConnection {
  private val derbyDriver =
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")

  def inDerby[A](op: Connection ⇒ A) =
    withConnection(DriverManager.getConnection("jdbc:derby:project/testdb"), op)

  private def withConnection[A](con: Connection, op: Connection ⇒ A) =
    try {
      op(con)
    } catch {
      case t: Throwable ⇒ t.printStackTrace()
    } finally {
      con.close()
    }
}
