package cielago.models

import java.sql.Connection

import anorm.{ ~, SQL }
import anorm.SqlParser.str

case class ListInfo(
  listId: String,
  accountName: String)

object ListInfo {

  val mapping = str("list_id") ~ str("account_name")

  val parsing = mapping map {
    case listId ~ accountName =>
      ListInfo(listId, accountName)
  }

  def all(implicit conn: Connection): List[ListInfo] =
    SQL("""
SELECT uuid AS list_id, login AS account_name FROM list_tbl
""") as (parsing *)

}
