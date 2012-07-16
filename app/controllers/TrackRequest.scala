package cielago.controllers

import java.util.Date

sealed case class TrackRequest(
  startDate: Option[Date],
  endDate: Option[Date],
  listId: Option[String])
