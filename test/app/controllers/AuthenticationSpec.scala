package app.controllers

import controllers.{TicTacToeController, routes}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.mvc.{Results, Security}
import play.api.test.Helpers._
import play.api.test._
import controllers.AuthenticationController._

class AuthenticationSpec
  extends PlaySpec
    with OneAppPerTest
    with Results {

  private val landingPageUrl = routes.TicTacToeController.index().url

  "TicTacToeController#registeredGame" when {

    "not logged in" should {
      "redirect to the landing page" in {
        val controller = app.injector.instanceOf[TicTacToeController]
        val resultFuture = controller.registeredGame().apply(FakeRequest())
        redirectLocation(resultFuture) must be(Some(landingPageUrl))
      }
    }

    "logged in" should {
      "not redirect to the landing page" in {

        val username = "username1"
        val password = "password1"

        val authenticationController = app.injector.instanceOf[controllers.AuthenticationController]
        val registerResultFuture = authenticationController.register().apply(FakeRequest().withFormUrlEncodedBody(
          USERNAME_FIELD -> username,
          PASSWORD_FIELD -> password,
          PASSWORD2_FIELD -> password))
        await(registerResultFuture)

        val controller = app.injector.instanceOf[TicTacToeController]
        val fakeRequest = FakeRequest().withSession(Security.username -> username)
        val resultFuture = controller.registeredGame().apply(fakeRequest)
        status(resultFuture) must be(OK)
      }
    }
  }
}
