package controllers

import javax.inject.{Inject, Named, Singleton}

import akka.actor.ActorRef
import akka.pattern.ask
import builders.MyActionBuilders
import defaults.Defaults._
import extensions.FormsExtensions._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import play.api.{Configuration, Logger}

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

  private def blankOutPasswordData[A](form: Form[A]) = {
    val data1 = form.data - PASSWORD_FIELD - PASSWORD2_FIELD
    val data2 = data1 + (PASSWORD_FIELD -> "") + (PASSWORD2_FIELD -> "")
    form.copy(data = data2)
  }

  private def removePasswordData[A](form: Form[A]) = {
    form.copy(data = form.data - PASSWORD_FIELD - PASSWORD2_FIELD)
  }

  def register = OptionallyAuthenticatedBuilder.async { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.translatedErrorMessages)
        Future.successful(BadRequest(views.html.registration(version, request.user)(blankOutPasswordData(formWithErrors))))
      },
      registrationData => {
        val filledForm = removePasswordData(registrationForm.fill(registrationData))
        if (registrationData.password != registrationData.password2) {
          val filledFormWithGlobalError = filledForm.withGlobalError("registrationForm.passwordMismatch")
          Logger.warn(filledFormWithGlobalError.translatedErrorMessages)
          Future.successful(BadRequest(views.html.registration(version, request.user)(filledFormWithGlobalError)))
        }
        else {
          val response = (mainActor ? RegisterUserRequest(registrationData.username, registrationData.password)).mapTo[RegisterUserResponse]
          response map {
            case RegisterUserResponse(Some(user)) =>
              Logger.info(s"Created new user: $user")
              Redirect(routes.TicTacToeController.registeredGame()).withSession(USERNAME_FIELD -> user.username)
            case RegisterUserResponse(None) =>
              val formError = FormError(USERNAME_FIELD, "registrationForm.usernameAlreadyExists", Seq(registrationData.username))
              val filledFormWithError = filledForm.withError(formError)
              Logger.warn(formError.translatedErrorMessages)
              BadRequest(views.html.registration(version, request.user)(filledFormWithError))
          }
        }
      }
    )
  }

  def login = OptionallyAuthenticatedBuilder.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.translatedErrorMessages)
        Future.successful(BadRequest(views.html.landingPage(version, request.user)(blankOutPasswordData(formWithErrors))))
      },
      loginData => {
        val filledForm = loginForm.fill(loginData).copy(data = Map())
        val response = (mainActor ? LoginRequest(loginData.username, loginData.password)).mapTo[LoginResponse]
        response map {
          case LoginResponse(Some(user)) =>
            Redirect(routes.TicTacToeController.registeredGame()).withSession(USERNAME_FIELD -> user.username)
          case LoginResponse(None) =>
            val filledFormWithGlobalError = filledForm.withGlobalError("loginForm.badLogin")
            Logger.warn(filledFormWithGlobalError.translatedErrorMessages)
            BadRequest(views.html.landingPage(version, request.user)(filledFormWithGlobalError))
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
      //.verifying("registrationForm.passwordMismatch", registrationData => registrationData.password == registrationData.password2)
  )

  case class LoginData(username: String, password: String)

  val loginForm = Form(
    mapping(
      USERNAME_FIELD -> nonEmptyText,
      PASSWORD_FIELD -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )
}
