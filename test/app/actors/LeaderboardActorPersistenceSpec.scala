package app.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest._

class LeaderboardActorPersistenceSpec extends TestKit(ActorSystem("LeaderboardActorPersistenceSpec"))
  with FlatSpecLike
  with Matchers
  with BeforeAndAfterAll
  with ImplicitSender {

  import actors.LeaderboardActor
  import actors.LeaderboardActor._
  import fixtures.RestartableActor
  import fixtures.RestartableActor._
  import models.Outcome._
  import models.LeaderboardEntry

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "finishing a game" should "preserve it after restarting the actor" in {

    akka.testkit.filterException[RestartActorException] {

      val leaderboardActor = system.actorOf(Props(new LeaderboardActor with RestartableActor))

      leaderboardActor ! GameFinished("username1", Player1Win)
      leaderboardActor ! GetLeadersRequest
      expectMsg(GetLeadersResponse(Seq(LeaderboardEntry("username1", 1, 0, 0))))

      leaderboardActor ! RestartActor

      leaderboardActor ! GetLeadersRequest
      expectMsg(GetLeadersResponse(Seq(LeaderboardEntry("username1", 1, 0, 0))))
    }
  }
}
