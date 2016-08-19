package controllers

import javax.inject._

import play.api.{Configuration, Logger}
import play.api.mvc._

@Singleton
class TicTacToeController @Inject()(configuration: Configuration) extends Controller {
  def index = Action {
    val version = configuration.getString("app.version")
    Logger.info(s"version: $version")
    Ok(views.html.tictactoe(version))
  }
}
