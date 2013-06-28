package cielago

import java.io.{ PrintWriter, StringWriter }

import util.control.Exception.allCatch

import scalaz.ValidationNel
import scalaz.syntax.validation.ToValidationV // .failureNel, .successNel

trait Cielago {
  type Failure = String

  type Valid[A] = ValidationNel[Failure, A]

  protected def stackTraceFailure(t: Throwable): Failure = {
    val buff = new StringWriter()
    val w = new PrintWriter(buff)

    try {
      t.printStackTrace(w)
      w.flush()

      buff.toString
    } catch {
      case _: Throwable ⇒ t.getMessage
    } finally {
      try {
        w.close()
      }
    }
  }

  def unsafe[A](op: ⇒ A)(implicit handler: Throwable ⇒ Failure = stackTraceFailure _): Valid[A] = (allCatch either op).fold(
    handler(_).failureNel[A], _.successNel[Failure])

}
