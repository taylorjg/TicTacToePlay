package controllers

import javax.inject._

import actors.LeaderboardActor._
import akka.actor.ActorRef
import akka.pattern.ask
import models.User
import defaults.Defaults._
import play.api.Configuration
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class TicTacToeController @Inject()(configuration: Configuration, @Named("mainActor") mainActor: ActorRef) extends Controller {

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

  object AuthenticatedBuilder extends ActionBuilder[({ type R[A] = AuthenticatedRequest[A, User] })#R] with Results {

    import actors.UsersActor.{LookupUsernameRequest, LookupUsernameResponse}

    private val indexCall = routes.TicTacToeController.index()

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A, User]) => Future[Result]): Future[Result] = {
      request.session.get("username") match {
        case Some(username) =>
          val response = (mainActor ? LookupUsernameRequest(username)).mapTo[LookupUsernameResponse]
          response flatMap {
            case LookupUsernameResponse(Some(user)) =>
              val authenticatedRequest = new AuthenticatedRequest(user, request)
              block(authenticatedRequest)
            case LookupUsernameResponse(None) => Future.successful(Redirect(indexCall).withNewSession)
          }
        case None => Future.successful(Redirect(indexCall).withNewSession)
      }
    }
  }
}
