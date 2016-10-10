package controllers

import javax.inject._

import actors.LeaderboardActor._
import akka.actor.ActorRef
import akka.pattern.ask
import builders.MyActionBuilders
import controllers.AuthenticationController._
import defaults.Defaults._
import modules.UserService
import play.api.Configuration
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class TicTacToeController @Inject()(@Named("mainActor") val mainActor: ActorRef,
                                    val userService: UserService,
                                    val messagesApi: MessagesApi,
                                    configuration: Configuration)
  extends Controller
  with I18nSupport
  with MyActionBuilders
{
  val version = configuration.getString("app.version") getOrElse "?"

  def index = OptionallyAuthenticatedBuilder { implicit request =>
    Ok(views.html.landingPage(version, request.user)(loginForm))
  }

  def registeredGame = AuthenticatedBuilder.async { implicit request =>
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(views.html.registeredGame(version, request.user, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = OptionallyAuthenticatedBuilder { implicit request =>
    Ok(views.html.unregisteredGame(version, request.user))
  }

  def registration = OptionallyAuthenticatedBuilder { implicit request =>
    Ok(views.html.registration(version, request.user)(registrationForm))
  }
}
