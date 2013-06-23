package cielago.controllers

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils.{ md5Hex ⇒ md5 }

import scalaz.{ Identitys, NonEmptyList, Options }

import play.api.db.DB

import play.api.Play.current

import play.api.mvc.{
  Action,
  AnyContent,
  Controller,
  SimpleResult,
  Request,
  Result
}

import cielago.models.ListInfo

trait Authenticated[+A] {
  val data: A
  val userDigest: String
}

trait CielagoController extends Controller with Options with Identitys {
  protected def get(name: String)(implicit request: Request[_]) =
    request.queryString get name flatMap { _.headOption }

  def SecureAction(block: (Authenticated[Request[AnyContent]]) ⇒ SimpleResult): Action[AnyContent] =
    Action { request ⇒
      val userDigest: Option[String] = request.
        session.get("userDigest") /* already logged in */ orElse {
          request.cookies.get("userDigest").map(_.value) orElse {
            request.headers.get("authorization").flatMap(basicDigest(_))
          } flatMap { digest /* request digest */ ⇒
            DB withConnection { implicit conn ⇒
              ListInfo.tracked(digest) map { _ ⇒ digest }
            }
          }
        }

      userDigest.fold(NoTrackerAvailable) { digest ⇒ 
        val authenticatedReq = new Authenticated[Request[AnyContent]] {
          override val data = request
          override val userDigest = digest
        }

        block(authenticatedReq) withSession {
          request.session + ("userDigest" -> digest)
        }
      }
    }

  protected val NoTrackerAvailable =
    Unauthorized(views.html.unauthorized()) withHeaders {
      "WWW-Authenticate" -> "Basic realm=\"Cielago\""
    }

  // e.g. Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==
  private def basicDigest(authHeader: String): Option[String] =
    (authHeader.indexOf(" ") match {
      case i if (i + 1 < authHeader.length) ⇒
        Some(authHeader.substring(i + 1))
      case _ ⇒ None
    }) flatMap { base64Digest ⇒ // e.g. QWxhZGRpbjpvcGVuIHNlc2FtZQ==
      Base64.decodeBase64(base64Digest.getBytes) |> { decodedBytes ⇒
        new String(decodedBytes) |> { str ⇒
          str.indexOf(":") match {
            case i if (i + 1 < str.length) ⇒
              Some(md5(str.substring(0, i) + ':' + md5(str.substring(i + 1))))

            case _ ⇒ None
          }
        }
      }
    }

}
