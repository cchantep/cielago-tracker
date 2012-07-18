package cielago.models

sealed trait OrderDirection {
  def code: String
}

case object AscendingOrder extends OrderDirection {
  def code = "ASC";
}

case object DescendingOrder extends OrderDirection {
  def code = "DESC";
}

case class OrderClause(column: String, direction: OrderDirection)
