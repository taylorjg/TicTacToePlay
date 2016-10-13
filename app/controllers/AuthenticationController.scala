package controllers

import javax.inject.{Inject, Singleton}

import builders.MyActionBuilders
import modules.UserService
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthenticationController @Inject()(val userService: UserService,
                                         val messagesApi: MessagesApi,
                                         configuration: Configuration)
  extends Controller
    with I18nSupport
    with MyActionBuilders {

  import AuthenticationController._
  import extensions.FormsExtensions._

  val version = configuration.getString("app.version") getOrElse "?"

  def register = OptionallyAuthenticatedBuilder.async { implicit request =>
    registrationForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.translatedErrorMessages)
        Future.successful(BadRequest(views.html.registration(version, request.user)(formWithErrors)))
      },
      registrationData => {
        userService.registerUser(registrationData.username, registrationData.password) map {
          case Some(user) =>
            Logger.info(s"Created new user: $user")
            Redirect(routes.TicTacToeController.registeredGame()).withSession(Security.username -> user.username)
          case None =>
            val filledForm = registrationForm.fill(registrationData)
            val formError = FormError(USERNAME_FIELD, "registrationForm.usernameAlreadyExists", Seq(registrationData.username))
            val filledFormWithError = filledForm.withError(formError)
            Logger.warn(formError.translatedErrorMessages)
            Ok(views.html.registration(version, request.user)(filledFormWithError))
        }
      }
    )
  }

  def login = OptionallyAuthenticatedBuilder.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.warn(formWithErrors.translatedErrorMessages)
        Future.successful(BadRequest(views.html.landingPage(version, request.user)(formWithErrors)))
      },
      loginData => {
        userService.login(loginData.username, loginData.password) map {
          case Some(user) =>
            Redirect(routes.TicTacToeController.registeredGame()).withSession(Security.username -> user.username)
          case None =>
            val filledForm = loginForm.fill(loginData)
            val filledFormWithGlobalError = filledForm.withGlobalError("loginForm.badLogin")
            Logger.warn(filledFormWithGlobalError.translatedErrorMessages)
            Ok(views.html.landingPage(version, request.user)(filledFormWithGlobalError))
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
      .verifying("registrationForm.passwordMismatch", registrationData => registrationData.password == registrationData.password2)
  )

  case class LoginData(username: String, password: String)

  val loginForm = Form(
    mapping(
      USERNAME_FIELD -> nonEmptyText,
      PASSWORD_FIELD -> nonEmptyText
    )(LoginData.apply)(LoginData.unapply)
  )
}
