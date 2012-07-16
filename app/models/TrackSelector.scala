package cielago.models

import java.util.Date

sealed trait TrackSelector

case class TrackPeriodSelector(
  startDate: Date,
  endDate: Date) extends TrackSelector

case class TrackListSelector(listId: String) extends TrackSelector

case class TrackPeriodListSelector(
  startDate: Date,
  endDate: Date,
  listId: String) extends TrackSelector
