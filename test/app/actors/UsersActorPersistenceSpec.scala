package app.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

class UsersActorPersistenceSpec extends TestKit(ActorSystem("UsersActorPersistenceSpec"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  import actors.UsersActor
  import actors.UsersActor._
  import fixtures.RestartableActor
  import fixtures.RestartableActor._
  import models.User

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "registering a new user" should "preserve it after restarting the actor" in {

    val usersActor1 = system.actorOf(Props(new UsersActor with RestartableActor))

    usersActor1 ! RegisterUserRequest("username1", "password1")
    expectMsgPF() { case RegisterUserResponse(Some(User("username1", _))) => true }

    usersActor1 ! RestartActor

    usersActor1 ! GetUsersRequest
    expectMsgPF() { case GetUsersResponse(Seq(User("username1", _))) => true }
  }
}
