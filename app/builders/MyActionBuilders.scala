package builders

import akka.actor.ActorRef
import akka.pattern.ask
import defaults.Defaults._
import controllers.routes
import models.User
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc.{ActionBuilder, Request, Result, Results}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait MyActionBuilders{

  def mainActor: ActorRef

  def AuthenticatedBuilder = new ActionBuilder[({ type R[A] = AuthenticatedRequest[A, User] })#R] with Results {

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
            case LookupUsernameResponse(None) =>
              Future.successful(Redirect(indexCall).withNewSession)
          }
        case None => Future.successful(Redirect(indexCall).withNewSession)
      }
    }
  }

  def OptionallyAuthenticatedBuilder = new ActionBuilder[({ type R[A] = AuthenticatedRequest[A, Option[User]] })#R] with Results {

    import actors.UsersActor.{LookupUsernameRequest, LookupUsernameResponse}

    private val indexCall = routes.TicTacToeController.index()

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A, Option[User]]) => Future[Result]): Future[Result] = {
      request.session.get("username") match {
        case Some(username) =>
          val response = (mainActor ? LookupUsernameRequest(username)).mapTo[LookupUsernameResponse]
          response flatMap {
            case LookupUsernameResponse(Some(user)) =>
              val authenticatedRequest = new AuthenticatedRequest(Option[User](user), request)
              block(authenticatedRequest)
            case LookupUsernameResponse(None) =>
              Future.successful(Redirect(indexCall).withNewSession)
          }
        case None =>
          val authenticatedRequest = new AuthenticatedRequest(Option.empty[User], request)
          block(authenticatedRequest)
      }
    }
  }
}
