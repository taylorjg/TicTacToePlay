package controllers

import javax.inject._

import play.api.{Configuration, Logger}
import play.api.mvc._
import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import actors.LeaderboardActor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TicTacToeController @Inject()(configuration: Configuration, @Named("mainActor") mainActor: ActorRef) extends Controller {

  implicit val timeout = Timeout(2 seconds)

  def index = Action {
    Redirect(routes.TicTacToeController.unregisteredGame().url)
  }

  def registeredGame = Action.async {
    val version = configuration.getString("app.version")
    Logger.info(s"version: $version")
    val future = (mainActor ? GetLeadersRequest(10)).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      println(s"leaders: ${getLeadersResponse.leaders}")
      Ok(views.html.registeredGame(version, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = Action {
    val version = configuration.getString("app.version")
    Logger.info(s"version: $version")
    Ok(views.html.unregisteredGame(version))
  }
}
