package controllers

import javax.inject._

import play.api.mvc._
import play.api.libs.json._
import models._
import play.api.Logger

@Singleton
class ApiController @Inject() extends Controller {

  val CROSS = 'X'
  val NOUGHT = 'O'
  val EMPTY = '-'

  val random = scala.util.Random

  implicit val gameStateReads = Json.reads[GameState]
  implicit val gameStateWrites = Json.writes[GameState]

  def computerMove = Action(parse.json) { implicit request =>

    val oldStateJson = request.body
    val oldState = oldStateJson.as[GameState]
    Logger.info(s"computerMove: oldState: $oldState")

    val newState = oldState.copy(board = makeRandomMove(oldState.board))
    Logger.info(s"computerMove: newState: $newState")

    Ok(Json.toJson(newState))
  }

  private def makeRandomMove(board: String): String = {
    val availableLocations = board.indices.filter(board(_) == EMPTY)
    if (availableLocations.isEmpty) board
    else {
      val randomAvailableLocationIndex = random.nextInt(availableLocations.indices.length)
      val randomAvailableLocation = availableLocations(randomAvailableLocationIndex)
      board.updated(randomAvailableLocation, NOUGHT)
    }
  }
}
