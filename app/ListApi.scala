package cielago

import play.api.Play.current

import play.api.db.DB

import models.ListInfo

object ListApi {

  def all: List[ListInfo] = DB withConnection { implicit conn ⇒
    ListInfo.all
  }

}
