package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.Logger
import models._

@Singleton
class ApiController @Inject() extends Controller {

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
