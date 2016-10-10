package builders

import controllers.routes
import models.User
import modules.UserService
import play.api.mvc.Security.AuthenticatedRequest
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait MyActionBuilders {

  val userService: UserService

  def AuthenticatedBuilder = new ActionBuilder[({type R[A] = AuthenticatedRequest[A, User]})#R] with Results {

    private val indexCall = routes.TicTacToeController.index()

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A, User]) => Future[Result]): Future[Result] = {
      request.session.get(Security.username) match {
        case Some(username) =>
            userService.lookupUsername(username) flatMap {
            case Some(user) =>
              val authenticatedRequest = new AuthenticatedRequest(user, request)
              block(authenticatedRequest)
            case None =>
              Future.successful(Redirect(indexCall).withNewSession)
          }
        case None => Future.successful(Redirect(indexCall).withNewSession)
      }
    }
  }

  def OptionallyAuthenticatedBuilder = new ActionBuilder[({type R[A] = AuthenticatedRequest[A, Option[User]]})#R] with Results {

    override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A, Option[User]]) => Future[Result]): Future[Result] = {
      request.session.get(Security.username) match {
        case Some(username) =>
          userService.lookupUsername(username) flatMap {
            userOption =>
              val authenticatedRequest = new AuthenticatedRequest(userOption, request)
              block(authenticatedRequest)
          }
        case None =>
          val authenticatedRequest = new AuthenticatedRequest(Option.empty[User], request)
          block(authenticatedRequest)
      }
    }
  }
}
