package cielago.models

case class Pagination(
  perPage: Int /* @todo low unsigned type check */ ,
  currentIndex: Int /* @todo low unsigned type check */ ,
  order: Seq[OrderClause] = Seq())

case class Paginated[A](
  pagination: Option[Pagination] = None,
  value: Seq[A] = Seq())
