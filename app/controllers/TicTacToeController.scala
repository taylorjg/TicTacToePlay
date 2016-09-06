package controllers

import javax.inject._

import actors.LeaderboardActor._
import akka.actor.ActorRef
import akka.pattern.ask
import models.User
import defaults.Defaults._
import play.api.Configuration
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TicTacToeController @Inject()(configuration: Configuration, @Named("mainActor") mainActor: ActorRef) extends Controller {

  val version = configuration.getString("app.version") getOrElse "?"

  def index = Action.async { implicit request =>
    Future.successful(Ok(views.html.landingPage(version)))
  }

  def registeredGame = AuthenticatedBuilder.async { implicit request =>
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(views.html.registeredGame(version, request.user.username, getLeadersResponse.leaders))
    }
  }

  def unregisteredGame = Action { implicit request =>
    Ok(views.html.unregisteredGame(version))
  }

  def registration = Action { implicit request =>
    Ok(views.html.registration(version))
  }

  case class AuthenticatedRequest[A](user: User, request: Request[A]) extends WrappedRequest[A](request)

  object AuthenticatedBuilder extends ActionBuilder[AuthenticatedRequest] with Results {
    import actors.UsersActor.{LookupUsernameRequest, LookupUsernameResponse}
    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
      val indexCall = routes.TicTacToeController.index()
      request.session.get("username") match {
        case Some(username) =>
          val response = (mainActor ? LookupUsernameRequest(username)).mapTo[LookupUsernameResponse]
          response flatMap {
            case LookupUsernameResponse(Some(user)) =>
              val authenticatedUser = AuthenticatedRequest[A](user, request)
              block(authenticatedUser)
            case LookupUsernameResponse(None) =>
              Future.successful(Redirect(indexCall).withNewSession)
          }
        case None => Future.successful(Redirect(indexCall).withNewSession)
      }
    }
  }
}
