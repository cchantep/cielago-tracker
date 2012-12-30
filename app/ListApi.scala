package cielago

import scalaz.NonEmptyList

import play.api.Play.current

import play.api.db.DB

import models.ListInfo

object ListApi {

  def tracked(userDigest: String) =
    DB withConnection { implicit conn ⇒ ListInfo.tracked(userDigest) }

}
