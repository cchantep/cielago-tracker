package cielago.models

import java.util.Date

import java.sql.Connection

import anorm.{ ~, SQL }
import anorm.SqlParser.{ long, str }

case class Pagination(
  perPage: Int /* @todo low unsigned type check */ ,
  currentIndex: Int /* @todo low unsigned type check */ )

case class MessageReport(
  list: ListInfo,
  messageId: String,
  subject: String,
  sendTime: Date,
  recipientCount: Long)

object MessageReport {

  val parsing = ListInfo.mapping ~
    str("message_id") ~
    str("subject") ~
    long("send_time") ~
    long("recipient_count") map {
      case listId ~
        accountName ~
        messageId ~
        subject ~
        sendTime ~
        recipientCount ⇒
        MessageReport(ListInfo(listId, accountName),
          messageId,
          subject,
          new Date(sendTime),
          recipientCount)
    }

  def find(selector: TrackSelector, pagination: Pagination)(implicit conn: Connection) = {
    println("pagination = %s" format pagination)

    val paginator = "LIMIT %s OFFSET %s".
      format(pagination.perPage,
        pagination.perPage * pagination.currentIndex);

    val sql = """
SELECT l.uuid AS list_id, 
  l.login AS account_name, 
  m.uuid AS message_id, 
  m.subject, 
  m.send_time,
  COUNT(r.uuid) AS recipient_count 
FROM list_tbl l 
JOIN message_tbl m 
  ON l.uuid=m.list_uuid 
JOIN recipient_tbl r 
  ON m.uuid=r.message_uuid 
WHERE m.type=0 AND %s 
GROUP BY l.uuid, 
  l.login, 
  m.uuid, 
  m.subject,
  m.send_time 
ORDER BY m.send_time ASC 
""" + paginator

    val rs = selector match {
      case a: TrackPeriodSelector ⇒
        SQL(sql format
          "m.send_time >= {startTime} AND m.send_time <= {endTime}").
          on("startTime" -> a.startDate.getTime,
            "endTime" -> a.endDate.getTime)

      case b: TrackListSelector ⇒
        SQL(sql format "l.uuid = {listId}").
          on("listId" -> b.listId)

      case c: TrackPeriodListSelector ⇒
        SQL(sql format
          """
m.send_time >= {startTime} 
  AND m.send_time <= {endTime} 
  AND l.uuid = {listId}
""").on("startTime" -> c.startDate.getTime,
          "endTime" -> c.endDate.getTime,
          "listId" -> c.listId)
    }

    rs.as(parsing *)
  }
}
