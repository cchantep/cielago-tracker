package cielago

import java.io.{ PrintWriter, StringWriter }

import util.control.Exception.allCatch

import scalaz.{ Identitys, Validation, Validations, NonEmptyList }

/**
 * Adapted from https://github.com/ornicar/scalalib/blob/master/src/main/scala/OrnicarValidation.scala
 */
trait Cielago extends Validations with Identitys {
  type Failures = NonEmptyList[String]

  type Valid[A] = Validation[Failures, A]

  protected def stackTraceFailure(t: Throwable): Failures = {
    val buff = new StringWriter()
    val w = new PrintWriter(buff)

    try {
      t.printStackTrace(w)
      w.flush()

      buff.toString.wrapNel
    } catch {
      case _: Throwable ⇒ t.getMessage.wrapNel
    } finally {
      try {
        w.close()
      }
    }
  }

  def unsafe[A](op: ⇒ A)(implicit handler: Throwable ⇒ Failures = stackTraceFailure _): Valid[A] =
    validation((allCatch either op).left map handler)

}
