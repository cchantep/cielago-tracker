package cielago.models

case class Pagination(
  perPage: Int /* @todo low unsigned type check */ ,
  currentIndex: Int /* @todo low unsigned type check */ ,
  order: Seq[OrderClause] = Nil)

case class Paginated[A](
  pagination: Option[Pagination] = None,
  value: Seq[A] = Nil)

object Pagination {

  def sqlOrder(clauses: Seq[OrderClause], colMap: Map[String, String]) =
    clauses.foldLeft("") { (s, c) ⇒
      colMap get c.column match {
        case None ⇒ s
        case Some(n) ⇒
          s match {
            case "" ⇒ "ORDER BY " + n + " " + c.direction.code
            case v  ⇒ v + ", " + n + " " + c.direction.code
          }
      }
    } // e.g. ORDER BY col [ASC|DESC], ...

}
