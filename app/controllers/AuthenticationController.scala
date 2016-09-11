package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import builders.MyActionBuilders
import defaults.Defaults._
import play.api.{Configuration, Logger}
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.MessagesApi
import play.api.i18n.I18nSupport
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
        if (registrationData.password != registrationData.password2) {
          val msg = "Password and confirmation password don't match"
          Logger.warn(msg)
          Future.successful(Ok(views.html.registration(version, request.user)(registrationForm.withGlobalError(msg))))
        }
        else {
          val response = (mainActor ? RegisterUserRequest(registrationData.username, registrationData.password)).mapTo[RegisterUserResponse]
          response map {
            case RegisterUserResponse(Some(user)) =>
              Logger.info(s"Created new user: $user")
              Redirect(routes.TicTacToeController.registeredGame()).withSession("username" -> user.username)
            case RegisterUserResponse(None) =>
              val msg = s"Username ${registrationData.username} already exists"
              Logger.warn(msg)
              Ok(views.html.registration(version, request.user)(registrationForm.withGlobalError(msg)))
          }
        }
      }
    )
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
