package modules

import javax.inject.Named

import akka.actor.ActorRef
import akka.pattern.ask
import com.google.inject.{AbstractModule, Inject}
import models.User
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.Future

trait UserService {
  def lookupUsername(username: String): Future[Option[User]]
  // registerUser ?
  // login ?
  // logout ?
}

class UserServiceImpl @Inject()(@Named("mainActor") val mainActor: ActorRef) extends UserService {

  import actors.UsersActor._
  import defaults.Defaults._
  import scala.concurrent.ExecutionContext.Implicits.global

  override def lookupUsername(username: String): Future[Option[User]] =
    (mainActor ? LookupUsernameRequest(username)).mapTo[LookupUsernameResponse] map {
      case LookupUsernameResponse(userOption) => userOption
    }
}

class AuthenticationModule extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl]).asEagerSingleton()
  }
}
