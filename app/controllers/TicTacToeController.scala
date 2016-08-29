package controllers

import javax.inject._

import actors.LeaderboardActor._
import akka.actor.ActorRef
import akka.pattern.ask
import defaults.Defaults._
import play.api.mvc._
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TicTacToeController @Inject()(configuration: Configuration, @Named("mainActor") mainActor: ActorRef) extends Controller {

  val version = {
    val version = configuration.getString("app.version")
    Logger.info(s"version: $version")
    version
  }

  def index = Action {
    Redirect(routes.TicTacToeController.unregisteredGame().url)
  }

  def registeredGame = Action.async { implicit request =>
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(views.html.registeredGame(version, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = Action {
    Ok(views.html.unregisteredGame(version))
  }
}
