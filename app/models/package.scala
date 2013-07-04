package cielago

import javax.mail.internet.InternetAddress

import scalaz.Validation
import scalaz.{ @@, Tag }
import scalaz.syntax.validation.ToValidationV

package object models {
  sealed trait ID
  def ID(s: String): String @@ ID = Tag[String, ID](s)
  type MessageId = String @@ ID
  type ListId = String @@ ID

  sealed trait Email
  type MailAddress = String @@ Email
  def ValidEmail(s: String): Validation[Throwable, MailAddress] =
    try {
      new InternetAddress(s)
      Tag[String, Email](s).success[Exception]
    } catch {
      case t: Throwable ⇒ t.failure[MailAddress]
    }

}
