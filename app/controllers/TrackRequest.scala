package cielago.controllers

import java.util.Date

sealed case class TrackRequest(
  startDate: Option[Date] = None,
  endDate: Option[Date] = None,
  listId: Option[String] = None,
  order: List[String] = List(),
  currentPage: Int = 0)
