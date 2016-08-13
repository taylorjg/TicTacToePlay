package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class TicTacToeController @Inject() extends Controller {
  def index = Action {
    Ok(views.html.tictactoe())
  }
}
