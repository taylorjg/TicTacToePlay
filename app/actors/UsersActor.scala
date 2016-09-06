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
      val capturedSender = sender()
      findUserByUsername(username) match {
        case Some(_) =>
          capturedSender ! RegisterUserResponse(None)
        case None =>
          val passwordHash = password.bcrypt
          val user = User(username, passwordHash)
          persist(user) { user =>
            users = applyEvent(user)
            capturedSender ! RegisterUserResponse(Some(user))
          }
      }
      persist(command) { event =>
        findUserByUsername(username) match {
          case Some(_) =>
            capturedSender ! RegisterUserResponse(None)
          case None =>
            val passwordHash = password.bcrypt
            val user = User(username, passwordHash)
            users = applyEvent(user)
            capturedSender ! RegisterUserResponse(Some(user))
        }
      }

    case command @ LoginRequest(username, password) => {
      val userOption = findUserByUsername(username) filter {
        case User(_, hash) => password.isBcrypted(hash)
      }
      sender ! LoginResponse(userOption)
    }
  }

  private def applyEvent: PartialFunction[User, Set[User]] = {
    case user: User => users + user
  }

  private def findUserByUsername(username: String): Option[User] = {
    val loweredUsername = username.toLowerCase()
    users find { _.username.toLowerCase() == loweredUsername }
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
