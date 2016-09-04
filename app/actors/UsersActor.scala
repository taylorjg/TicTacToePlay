package actors

import akka.actor.Props
import akka.persistence.{PersistentActor, RecoveryCompleted}
import models.User
import play.api.Logger
import com.github.t3hnar.bcrypt._

class UsersActor extends PersistentActor {

  import UsersActor._

  var users: Set[User] = Set()

  override def persistenceId: String = self.path.name

  override def receiveRecover: Receive = {
    case event: User => {
      Logger.info(s"[UsersActor.receiveRecover] event: $event")
      users = applyEvent(event)
    }
    case RecoveryCompleted => Logger.info("[UsersActor.receiveRecover] RecoveryCompleted")
  }

  override def receiveCommand: Receive = {

    case command @ RegisterUserRequest(username, password) =>
      persist(command) { event =>
        if (users exists (_.username == username)) {
          println(s"username $username already exists")
          sender() ! RegisterUserResponse(None)
        }
        val passwordHash = password.bcrypt
        val user = User(username, passwordHash)
        users = applyEvent(user)
        sender() ! RegisterUserResponse(Some(user))
      }

    case command @ LoginRequest(username, password) => {
      val userOption = users find (_.username == username) filter {
        case User(_, hash) => password.isBcrypted(hash)
      }
      sender ! LoginResponse(userOption)
    }
  }

  private def applyEvent: PartialFunction[User, Set[User]] = {
    case user: User => users + user
  }
}

object UsersActor {

  case class RegisterUserRequest(username: String, password: String)
  case class RegisterUserResponse(user: Option[User])

  case class LoginRequest(username: String, password: String)
  case class LoginResponse(user: Option[User])

  def props: Props = {
    Props(classOf[UsersActor])
  }
}
