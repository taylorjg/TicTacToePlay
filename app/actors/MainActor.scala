package actors

import akka.actor.{Actor, Props}
import models.GameState

class MainActor extends Actor {

  val unregisteredMoveEngine = context.actorOf(UnregisteredMoveEngineActor.props, "UnregisteredMoveEngine")
  val leaderboard = context.actorOf(LeaderboardActor.props, "Leaderboard")

  override def receive: Receive = {
    case oldState: GameState => {
      val moveEngine = oldState.username match {
        case Some(username) => {
          lazy val newRegisteredMoveEngine = context.actorOf(RegisteredMoveEngineActor.props(leaderboard), username)
          context.child(username) getOrElse newRegisteredMoveEngine
        }
        case None => unregisteredMoveEngine
      }
      moveEngine forward oldState
    }
  }
}

object MainActor {
  def props: Props = {
    Props(classOf[MainActor])
  }
}