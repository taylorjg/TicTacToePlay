import akka.actor.{ActorSystem, PoisonPill}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

class UsersActorPersistenceSpec extends TestKit(ActorSystem("UsersActorPersistenceSpec"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  import actors.UsersActor
  import actors.UsersActor._
  import models.User

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  private val actorName = "UsersActor"

  "registering a new user" should "preserve it after restarting the actor" in {

    val usersActor1 = system.actorOf(UsersActor.props, actorName)
    usersActor1 ! RegisterUserRequest("username1", "password1")
    expectMsgPF() { case RegisterUserResponse(Some(User("username1", _))) => true }

    usersActor1 ! PoisonPill
    Thread.sleep(50)

    val usersActor2 = system.actorOf(UsersActor.props, actorName)
    usersActor2 ! GetUsersRequest
    expectMsgPF() { case GetUsersResponse(Seq(User("username1", _))) => true }
  }
}
