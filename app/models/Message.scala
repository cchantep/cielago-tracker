package cielago.models

import java.util.Date

import java.sql.Connection

import scalaz.{ Failure, Success, Validation }
import scalaz.syntax.validation.ToValidationV

import anorm.{
  Error ⇒ AnormError,
  Success ⇒ AnormSuccess,
  SQL,
  SqlRow,
  ~
}
import anorm.SqlParser.{ bool, long, str }

case class MessageContent(
  text: String,
  mime: String)

case class Sender(
  email: MailAddress,
  displayName: Option[String])

case class Message(
  id: MessageId,
  sendTime: Date,
  listId: ListId,
  subject: String,
  content: MessageContent,
  receipt: Boolean,
  sender: Sender)

object Message {
  private val mapping = str("uuid") ~
    long("send_time") ~
    str("list_uuid") ~
    str("subject") ~
    str("content_type") ~
    str("content") ~
    bool("receipt") ~
    str("sender_email") ~
    str("sender_dn").?

  private val parsing = mapping map {
    case id ~ time ~ listId ~ subject ~ contentType ~
      content ~ receipt ~ email ~ dn ⇒ ValidEmail(email) map { e ⇒
      Message(
        ID(id),
        new Date(time),
        ID(listId),
        subject,
        MessageContent(content, contentType),
        receipt,
        Sender(e, dn))
    }
  }

  def pending(implicit conn: Connection): Validation[Throwable, List[Message]] = {
    @annotation.tailrec
    def go(s: Stream[SqlRow], l: List[Message]): Validation[Throwable, List[Message]] = s.headOption match {
      case None ⇒ l.success[Throwable]
      case Some(r) ⇒ parsing(r) match {
        case AnormError(err) ⇒
          new RuntimeException(s"Persistence error: $err").
            failure[List[Message]]

        case AnormSuccess(Failure(t))   ⇒ t.failure[List[Message]]
        case AnormSuccess(Success(msg)) ⇒ go(s.tail, l :+ msg)
      }
    }

    go(SQL("""SELECT uuid, send_time, subject, content_type, content,
         receipt, sender_email, sender_dn 
       FROM message_tbl 
       WHERE notification_id > 0 
       ORDER BY send_time""")(), Nil)

  }
}
