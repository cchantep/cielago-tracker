package cielago.models

import java.util.Date

import java.sql.Connection

import anorm.{ ~, SQL }
import anorm.SqlParser.{ date, long, scalar }

case class DispatchReport(messages: Long, recipients: Long)

object DispatchReport {
  val parsing = long("message_count") ~
    long("recipient_count") map {
      case messages ~ recipients ⇒
        DispatchReport(messages, recipients)
    }

  def report(userDigest: String, selector: TrackSelector)(implicit conn: Connection): DispatchReport = {
    val sql = """
      SELECT COUNT(DISTINCT m.uuid) AS message_count,
        COUNT(r.uuid) AS recipient_count 
      FROM message_tbl m 
      JOIN recipient_tbl r 
        ON m.uuid=r.message_uuid 
      WHERE m.type=0
        AND %s 
    """

    val rs = selector match {
      case a: TrackPeriodSelector ⇒
        SQL(sql format """
          m.send_time >= {startTime} AND m.send_time <= {endTime} 
          AND EXISTS (SELECT NULL FROM trackers t 
            WHERE MD5(t.username || ':' || t.md5_secret) = {digest} 
            AND t.list_uuid=m.list_uuid)""").
          on('startTime -> a.startDate.getTime,
            'endTime -> a.endDate.getTime,
            'digest -> userDigest)

      case b: TrackListSelector ⇒
        SQL(sql format "m.list_uuid = {listId}").
          on('listId -> b.listId)

      case c: TrackPeriodListSelector ⇒
        SQL(sql format """
            m.send_time >= {startTime} 
              AND m.send_time <= {endTime} 
              AND m.list_uuid = {listId}
            """).on('startTime -> c.startDate.getTime,
          'endTime -> c.endDate.getTime,
          'listId -> c.listId)
    }

    rs.as(parsing.single)
  }
}
