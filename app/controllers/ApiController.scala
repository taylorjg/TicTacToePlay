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

  implicit object CharReads extends Reads[Char] {
    def reads(json: JsValue) = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.head)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("expected a string of length 1"))))
    }
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char) = JsString(o.toString)
  }

  implicit val gameStateReads = Json.reads[GameState]
  implicit val gameStateWrites = Json.writes[GameState]

  implicit val timeout = Timeout(5 seconds)

  def unregisteredComputerMove = Action.async(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"unregisteredComputerMove: oldState: $oldState")

    val future = (mainActor ? oldState).mapTo[GameState]
    future map { newState =>
      Logger.info(s"unregisteredComputerMove: newState: $newState")
      Ok(Json.toJson(newState))
    }
  }

  def registeredComputerMove = Action.async(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"registeredComputerMove: oldState: $oldState")

    val future = (mainActor ? oldState).mapTo[GameState]
    future map { newState =>
      Logger.info(s"registeredComputerMove: newState: $newState")
      Ok(Json.toJson(newState))
    }
  }
}
