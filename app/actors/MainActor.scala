package actors

import akka.actor.{Actor, Props}
import models.GameState

class MainActor extends Actor {

  import actors.LeaderboardActor.GetLeadersRequest
  import actors.LeaderboardUpdatesActor.SubscribeForLeaderboardUpdates

  val unregisteredMoveEngine = context.actorOf(UnregisteredMoveEngineActor.props, "UnregisteredMoveEngine")
  val leaderboard = context.actorOf(LeaderboardActor.props, "Leaderboard")

  override def receive: Receive = {

    case oldState: GameState =>
      val moveEngine = oldState.username match {
        case Some(username) =>
          val enc = java.nio.charset.StandardCharsets.UTF_8.name()
          val encodedUsername = java.net.URLEncoder.encode(username, enc)
          lazy val newRegisteredMoveEngine = context.actorOf(RegisteredMoveEngineActor.props(leaderboard), encodedUsername)
          context.child(encodedUsername) getOrElse newRegisteredMoveEngine
        case None => unregisteredMoveEngine
      }
      moveEngine forward oldState

    case GetLeadersRequest => leaderboard forward GetLeadersRequest

    case SubscribeForLeaderboardUpdates => leaderboard forward SubscribeForLeaderboardUpdates
  }
}

object MainActor {
  def props: Props = {
    Props(classOf[MainActor])
  }
}
