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

  def index = OptionallyAuthenticatedBuilder.async { implicit request =>
    play.api.Logger.info(s"user: ${request.user}")
    Future.successful(Ok(views.html.landingPage(version, request.user)))
  }

  def registeredGame = AuthenticatedBuilder.async { implicit request =>
    play.api.Logger.info(s"user: ${request.user}")
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(views.html.registeredGame(version, request.user, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = OptionallyAuthenticatedBuilder { implicit request =>
    play.api.Logger.info(s"user: ${request.user}")
    Ok(views.html.unregisteredGame(version, request.user))
  }

  def registration = OptionallyAuthenticatedBuilder { implicit request =>
    play.api.Logger.info(s"user: ${request.user}")
    Ok(views.html.registration(version, request.user))
  }
}
