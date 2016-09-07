package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import defaults.Defaults._
import play.api.Logger
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthenticationController @Inject()(@Named("mainActor") mainActor: ActorRef)
  extends Controller {

  import AuthenticationController._
  import actors.UsersActor._

  def register = Action.async(parse.form(registrationForm)) { implicit request =>
    val registrationData = request.body
    if (registrationData.password != registrationData.password2) {
      // TODO: set a global form error
    }
    val response = (mainActor ? RegisterUserRequest(registrationData.username, registrationData.password)).mapTo[RegisterUserResponse]
    response map {
      case RegisterUserResponse(Some(user)) => {
        Logger.info(s"new user: $user")
        Redirect(routes.TicTacToeController.registeredGame())
      }
      case RegisterUserResponse(None) => {
        Logger.warn(s"user with username ${registrationData.username} already exists")
        // TODO: set a global form error
        Redirect(routes.TicTacToeController.registration())
      }
    }
  }

  def login = Action.async(parse.form(loginForm)) { implicit request =>
    val loginData = request.body
    val response = (mainActor ? LoginRequest(loginData.username, loginData.password)).mapTo[LoginResponse]
    response map {
      case LoginResponse(Some(user)) =>
        Redirect(routes.TicTacToeController.registeredGame()).withSession("username" -> user.username)
      case LoginResponse(None) =>
        Redirect(routes.TicTacToeController.index())
    }
  }

  def logout = Action { implicit request =>
    Redirect(routes.TicTacToeController.index()).withNewSession
  }
}

object AuthenticationController {

  case class RegistrationData(username: String, password: String, password2: String)

  val registrationForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "password2" -> nonEmptyText
    )(RegistrationData.apply)(RegistrationData.unapply)
  )

  case class LoginData(username: String, password: String)

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )
}
