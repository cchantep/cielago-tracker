package cielago.models

import java.util.Date

import java.sql.Connection

import anorm.{ ~, SQL }
import anorm.SqlParser.{ date, long, scalar }

object DispatchReport {

  def recipientCount(selector: TrackSelector)(implicit conn: Connection): Long = {
    val sql = """
SELECT COUNT(r.uuid) AS recipient_count 
FROM list_tbl l, recipient_tbl r 
WHERE EXISTS (SELECT NULL FROM message_tbl m 
WHERE m.type=0
  AND m.list_uuid=l.uuid 
  AND m.uuid=r.message_uuid 
  AND %s) 
"""

    val rs = selector match {
      case a: TrackPeriodSelector ⇒
        SQL(sql format
          "m.send_time >= {startTime} AND m.send_time <= {endTime}").
          on("startTime" -> a.startDate.getTime,
            "endTime" -> a.endDate.getTime)

      case b: TrackListSelector ⇒
        SQL(sql format "m.list_uuid = {listId}").
          on("listId" -> b.listId)

      case c: TrackPeriodListSelector ⇒
        SQL(sql format
          """
m.send_time >= {startTime} 
  AND m.send_time <= {endTime} 
  AND m.list_uuid = {listId}
""").on("startTime" -> c.startDate.getTime,
          "endTime" -> c.endDate.getTime,
          "listId" -> c.listId)
    }

    rs.as(scalar[Long].single)
  }
}
