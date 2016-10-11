package app.controllers

import controllers.{TicTacToeController, routes}
import org.scalatestplus.play.{OneAppPerTest, PlaySpec}
import play.api.mvc.{Results, Security}
import play.api.test.Helpers._
import play.api.test._
import models.User
import modules.UserService
import org.scalatest.TestData
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.inject.bind

import scala.concurrent.Future

class AuthenticationSpec
  extends PlaySpec
    with OneAppPerTest
    with Results
    with UserService {

  var lookupUsernameResult: Future[Option[User]] = _

  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder()
      .overrides(bind[UserService].toInstance(this))
      .build
  }

  override def lookupUsername(username: String): Future[Option[User]] = lookupUsernameResult

  private val landingPageUrl = routes.TicTacToeController.index().url

  "TicTacToeController#registeredGame" when {

    "session does not contain a username" should {
      "redirect to the landing page" in {
        lookupUsernameResult = Future.successful(None)
        val controller = app.injector.instanceOf[TicTacToeController]
        val resultFuture = controller.registeredGame().apply(FakeRequest())
        status(resultFuture) must be(SEE_OTHER)
        redirectLocation(resultFuture) must be(Some(landingPageUrl))
      }
    }

    "session contains a valid username" should {
      "not redirect to the landing page" in {
        val username = "testuser1"
        lookupUsernameResult = Future.successful(Some(User(username, "DontCarePasswordHash")))
        val controller = app.injector.instanceOf[TicTacToeController]
        val fakeRequest = FakeRequest().withSession(Security.username -> username)
        val resultFuture = controller.registeredGame().apply(fakeRequest)
        status(resultFuture) must be(OK)
      }
    }
  }
}
