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
import scala.concurrent.ExecutionContext.Implicits.global

object MockUserService {
  private final val MOCK_USERS = Seq(
    User("testuser1", "not-used-password-hash"),
    User("testuser2", "not-used-password-hash"),
    User("testuser3", "not-used-password-hash"))
}

class MockUserService extends UserService {
  import MockUserService.MOCK_USERS
  override def lookupUsername(username: String): Future[Option[User]] =
    Future { MOCK_USERS.find(_.username == username) }
}

class AuthenticationSpec extends PlaySpec
  with OneAppPerTest
  with Results {

  override def newAppForTest(testData: TestData): Application = {
    new GuiceApplicationBuilder()
      .overrides(bind[UserService].to[MockUserService])
      .build
  }

  private val landingPageUrl = routes.TicTacToeController.index().url

  "TicTacToeController#registeredGame" when {

    "processing a request with no session" should {
      "redirect to the landing page" in {
        val controller = app.injector.instanceOf[TicTacToeController]
        val resultFuture = controller.registeredGame().apply(FakeRequest())
        status(resultFuture) must be(SEE_OTHER)
        redirectLocation(resultFuture) must be(Some(landingPageUrl))
      }
    }

    "processing a request with a session containing an invalid username" should {
      "redirect to the landing page" in {
        val controller = app.injector.instanceOf[TicTacToeController]
        val fakeRequest = FakeRequest().withSession(Security.username -> "testuser4")
        val resultFuture = controller.registeredGame().apply(fakeRequest)
        status(resultFuture) must be(SEE_OTHER)
        redirectLocation(resultFuture) must be(Some(landingPageUrl))
      }
    }

    "processing a request with a session containing a valid username" should {
      "not redirect to the landing page" in {
        val controller = app.injector.instanceOf[TicTacToeController]
        val fakeRequest = FakeRequest().withSession(Security.username -> "testuser1")
        val resultFuture = controller.registeredGame().apply(fakeRequest)
        status(resultFuture) must be(OK)
      }
    }
  }
}
