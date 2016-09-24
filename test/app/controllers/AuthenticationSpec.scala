package app.controllers

import controllers.{TicTacToeController, routes}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test._

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
  }
}
