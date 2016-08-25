package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{GameState, MoveEngine}
import play.api.Logger

class RegisteredMoveEngineActor(leaderboard: ActorRef) extends Actor {
  import actors.LeaderboardActor._
  Logger.info(s"MoveEngineActor ${context.self.path}")
  override def receive: Receive = {
    case oldState: GameState => {
      val newState = MoveEngine.computerMove(oldState)
      sender ! newState
      if (newState.outcome.isDefined) {
        leaderboard ! GameFinished(newState)
      }
    }
  }
}

object RegisteredMoveEngineActor {
  def props(leaderboard: ActorRef): Props = {
    Props(classOf[RegisteredMoveEngineActor], leaderboard)
  }
}
