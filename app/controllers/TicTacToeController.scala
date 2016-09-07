package controllers

import javax.inject._

import actors.LeaderboardActor._
import akka.actor.ActorRef
import akka.pattern.ask
import builders.MyActionBuilders
import defaults.Defaults._
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TicTacToeController @Inject()(configuration: Configuration, @Named("mainActor") val mainActor: ActorRef)
  extends Controller
  with MyActionBuilders
{

  val version = configuration.getString("app.version") getOrElse "?"

  def index = Action.async { implicit request =>
    Future.successful(Ok(views.html.landingPage(version)))
  }

  def registeredGame = AuthenticatedBuilder.async { implicit authenticatedRequest =>
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(views.html.registeredGame(version, authenticatedRequest.user.username, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = Action { implicit request =>
    Ok(views.html.unregisteredGame(version))
  }

  def registration = Action { implicit request =>
    Ok(views.html.registration(version))
  }
}
