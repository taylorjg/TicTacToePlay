package controllers

import javax.inject.{Inject, Named, Singleton}

import actors.UsersActor.{RegisterUserRequest, RegisterUserResponse}
import akka.actor.ActorRef
import akka.pattern.ask
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import defaults.Defaults._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AuthenticationController @Inject()(@Named("mainActor") mainActor: ActorRef)
  extends Controller {

  import AuthenticationController._

  def register = Action.async(parse.form(registrationForm)) { implicit request =>
    val registrationData = request.body
    if (registrationData.password != registrationData.password2) {
      // TODO: set a global form error
    }
    val response = (mainActor ? RegisterUserRequest(registrationData.username, registrationData.password)).mapTo[RegisterUserResponse]
    response map {
      case RegisterUserResponse(Some(user)) => {
        play.api.Logger.info(s"new user: $user")
        Redirect(routes.TicTacToeController.registeredGame())
      }
      case _ => {
        play.api.Logger.warn(s"user with username ${registrationData.username} already exists")
        // TODO: set a global form error
        Redirect(routes.TicTacToeController.registeredGame())
      }
    }
  }

  def login = Action.async { request =>
    Future.successful(Ok(Json.toJson(Map("dummy" -> ""))))
  }

  def logout = Action.async { request =>
    Future.successful(Ok(Json.toJson(Map("dummy" -> ""))))
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
}
