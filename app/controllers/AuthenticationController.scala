package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import builders.MyActionBuilders
import defaults.Defaults._
import play.api.{Configuration, Logger}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticationController @Inject()(@Named("mainActor") val mainActor: ActorRef,
                                         val messagesApi: MessagesApi,
                                         configuration: Configuration)
  extends Controller
  with I18nSupport
  with MyActionBuilders {

  import AuthenticationController._
  import actors.UsersActor._

  val version = configuration.getString("app.version") getOrElse "?"

  def register = OptionallyAuthenticatedBuilder.async { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.errors.mkString(", "))
        Future.successful(BadRequest(views.html.registration(version, request.user)(formWithErrors)))
      },
      registrationData => {
        val filledForm = registrationForm.fill(registrationData)
        if (registrationData.password != registrationData.password2) {
          val msg = Messages("registrationForm.passwordMismatch")
          Logger.warn(msg)
          Future.successful(Ok(views.html.registration(version, request.user)(filledForm.withGlobalError(msg))))
        }
        else {
          val response = (mainActor ? RegisterUserRequest(registrationData.username, registrationData.password)).mapTo[RegisterUserResponse]
          response map {
            case RegisterUserResponse(Some(user)) =>
              Logger.info(s"Created new user: $user")
              Redirect(routes.TicTacToeController.registeredGame()).withSession(USERNAME_FIELD -> user.username)
            case RegisterUserResponse(None) =>
              val msg = Messages("registrationForm.usernameAlreadyExists", registrationData.username)
              Logger.warn(msg)
              val formError = FormError(USERNAME_FIELD, msg)
              Ok(views.html.registration(version, request.user)(filledForm.withError(formError)))
          }
        }
      }
    )
  }

  def login = OptionallyAuthenticatedBuilder.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.errors.mkString(", "))
        Future.successful(BadRequest(views.html.landingPage(version, request.user)(formWithErrors)))
      },
      loginData => {
        val filledForm = loginForm.fill(loginData)
        val response = (mainActor ? LoginRequest(loginData.username, loginData.password)).mapTo[LoginResponse]
        response map {
          case LoginResponse(Some(user)) =>
            Redirect(routes.TicTacToeController.registeredGame()).withSession(USERNAME_FIELD -> user.username)
          case LoginResponse(None) =>
            val msg = Messages("loginForm.badLogin")
            Logger.warn(msg)
            Ok(views.html.landingPage(version, request.user)(filledForm.withGlobalError(msg)))
        }
      }
    )
  }

  def logout = Action { implicit request =>
    Redirect(routes.TicTacToeController.index()).withNewSession
  }
}

object AuthenticationController {

  val USERNAME_FIELD = "username"
  val PASSWORD_FIELD = "password"
  val PASSWORD2_FIELD = "password2"

  case class RegistrationData(username: String, password: String, password2: String)

  val registrationForm = Form(
    mapping(
      USERNAME_FIELD -> nonEmptyText,
      PASSWORD_FIELD -> nonEmptyText,
      PASSWORD2_FIELD -> nonEmptyText
    )(RegistrationData.apply)(RegistrationData.unapply)
  )

  case class LoginData(username: String, password: String)

  val loginForm = Form(
    mapping(
      USERNAME_FIELD -> nonEmptyText,
      PASSWORD_FIELD -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )
}
