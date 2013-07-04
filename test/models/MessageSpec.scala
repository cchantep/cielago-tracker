package cielago.models

import javax.mail.internet.AddressException

import scalaz.syntax.validation.ToValidationV

import org.specs2.mutable.Specification

object MessageSpec extends Specification with MessageFixtures {
  "Message" title

  "Pending messages" should {
    "not be found" in {
      Message.pending(noCon) aka "messages" mustEqual Nil.success[Throwable]
    }

    "be found" in {
      Message.pending(pendingCon).
        aka("messages") mustEqual twoPending.success[Throwable]
    }

    "detect invalid email" in {
      Message.pending(invalidEmailCon) aka "messages" must beLike {
        case scalaz.Failure(t) ⇒
          t.getClass aka "exception type" mustEqual classOf[AddressException]
      }
    }
  }
}

sealed trait MessageFixtures {
  import java.util.Date
  import scalaz.{ @@, Tag }
  import acolyte.RowLists.rowList9
  import acolyte.Rows.row9
  import acolyte.{ RowList, Row9 }
  import acolyte.Acolyte._

  val twoPending = List(
    Message(ID("id1"),
      new Date(),
      ID("listId"),
      "subject1",
      MessageContent("text/plain", "Pending 1"),
      true,
      Sender(Tag[String, Email]("sender@email.st"), None)),
    Message(ID("id2"),
      sendTime = new Date(),
      ID("listId"),
      "subject2",
      MessageContent("text/html", "<body>Pending 2</body>"),
      false,
      Sender(Tag[String, Email]("sender@cielago"), Some("Sender"))))

  val noPendingRow = rowList9(
    classOf[String] -> "uuid",
    classOf[Long] -> "send_time",
    classOf[String] -> "list_uuid",
    classOf[String] -> "subject",
    classOf[String] -> "content_type",
    classOf[String] -> "content",
    classOf[Boolean] -> "receipt",
    classOf[String] -> "sender_email",
    classOf[String] -> "sender_dn")

  lazy val noCon = connection(handleStatement.
    withQueryDetection("^SELECT ").
    withQueryHandler({ e: acolyte.Execution ⇒ noPendingRow.asResult }))

  lazy val pendingCon = connection(handleStatement.
    withQueryDetection("^SELECT ").
    withQueryHandler({ e: acolyte.Execution ⇒
      twoPending.foldLeft(noPendingRow) { (l, p) ⇒
        l :+ row9(p.id, p.sendTime.getTime, p.listId, p.subject,
          p.content.mime, p.content.text, p.receipt,
          p.sender.email, p.sender.displayName.getOrElse(null))
      }.asResult
    }))

  lazy val invalidEmailCon = connection(handleStatement.
    withQueryDetection("^SELECT ").
    withQueryHandler({ e: acolyte.Execution ⇒
      (noPendingRow :+ row9("id", System.currentTimeMillis,
        "listId", "subject", "text/plain", "Text", true,
        "@", null)).asResult

    }))

}
