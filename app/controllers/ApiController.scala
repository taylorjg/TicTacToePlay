package controllers

import javax.inject._

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import models._
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class ApiController @Inject()(@Named("mainActor") mainActor: ActorRef) extends Controller {

  import JsonFormatters._

  implicit val timeout = Timeout(2 seconds)

  def computerMove = Action.async(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"computerMove: oldState: $oldState")

    val future = (mainActor ? oldState).mapTo[GameState]
    future map { newState =>
      Logger.info(s"computerMove: newState: $newState")
      Ok(Json.toJson(newState))
    }
  }
}

object JsonFormatters {

  implicit object CharReads extends Reads[Char] {
    def reads(json: JsValue) = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.head)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("expected a string of length 1"))))
    }
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char) = JsString(o.toString)
  }

  implicit val gameStateReads: Reads[GameState] = Json.reads[GameState]
  implicit val gameStateWrites: Writes[GameState] = Json.writes[GameState]
}
