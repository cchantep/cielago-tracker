package cielago

import util.control.Exception.allCatch

import scalaz.{ Identitys, Validation, Validations, NonEmptyList }

/**
 * Adapted from https://github.com/ornicar/scalalib/blob/master/src/main/scala/OrnicarValidation.scala
 */
trait Cielago extends Validations with Identitys {
  type Failures = NonEmptyList[String]

  type Valid[A] = Validation[Failures, A]

  protected def makeFailures(e: Any): Failures = e match {
    case e: Throwable       ⇒ e.getMessage wrapNel
    case m: NonEmptyList[_] ⇒ m map (_.toString)
    case s                  ⇒ s.toString wrapNel
  }

  private def eitherToValidation[E, B](either: Either[E, B]): Valid[B] =
    validation(either.left map makeFailures)

  def unsafe[A](op: ⇒ A)(implicit handler: Throwable ⇒ String = _.getMessage): Valid[A] =
    eitherToValidation((allCatch either op).left map handler)

}
