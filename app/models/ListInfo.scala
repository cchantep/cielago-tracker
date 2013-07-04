package cielago.models

import java.sql.Connection

import scalaz.NonEmptyList
import scalaz.syntax.std.list.ToListOpsFromList // .toNel

import anorm.{ ~, SQL }
import anorm.SqlParser.str

case class ListInfo(
  listId: ListId,
  accountName: String)

object ListInfo {
  private[models] val mapping = str("list_id") ~ str("account_name")

  private val parsing = mapping map {
    case listId ~ accountName ⇒ ListInfo(ID(listId), accountName)
  }

  def tracked(userDigest: String)(implicit conn: Connection): Option[NonEmptyList[ListInfo]] = SQL("""
        SELECT list_uuid AS list_id, l.login AS account_name 
        FROM trackers t JOIN list_tbl l ON t.list_uuid=l.uuid 
        WHERE MD5(t.username || ':' || t.md5_secret) = {digest}
        """).on('digest -> userDigest).as(parsing.*).toNel

}
