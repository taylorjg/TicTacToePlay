package services

import javax.inject.Named

import akka.actor.ActorRef
import com.google.inject.Inject

class UserServiceImpl @Inject()(@Named("mainActor") val mainActor: ActorRef) extends UserService {

  import actors.UsersActor._
  import akka.pattern.ask
  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global
  import defaults.Defaults.ASK_TIMEOUT
  import models.User

  override def lookupUsername(username: String): Future[Option[User]] =
    (mainActor ? LookupUsernameRequest(username)).mapTo[LookupUsernameResponse] map (_.user)

  override def login(username: String, password: String): Future[Option[User]] =
    (mainActor ? LoginRequest(username, password)).mapTo[LoginResponse] map (_.user)

  override def registerUser(username: String, password: String): Future[Option[User]] =
    (mainActor ? RegisterUserRequest(username, password)).mapTo[RegisterUserResponse] map (_.user)
}
