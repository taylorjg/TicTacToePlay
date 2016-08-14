package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import play.api.data.validation.ValidationError
import models._

@Singleton
class ApiController @Inject() extends Controller {

  implicit object CharReads extends Reads[Char] {
    def reads(json: JsValue) = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.head)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("expected "))))
    }
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char) = JsString(o.toString)
  }

  implicit val gameStateReads = Json.reads[GameState]
  implicit val gameStateWrites = Json.writes[GameState]

  def computerMove = Action(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"computerMove: oldState: $oldState")

    val newState = MoveEngine.computerMove(oldState)
    Logger.info(s"computerMove: newState: $newState")

    Ok(Json.toJson(newState))
  }
}
