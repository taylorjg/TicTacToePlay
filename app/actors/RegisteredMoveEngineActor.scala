package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{GameState, MoveEngine}

class RegisteredMoveEngineActor(leaderboard: ActorRef) extends Actor {
  import actors.LeaderboardActor._
  override def receive: Receive = {
    case oldState: GameState =>
      val newState = MoveEngine.computerMove(oldState)
      sender ! newState
      (newState.username, newState.outcome) match {
        case (Some(username), Some(outcome)) => leaderboard ! GameFinished(username, outcome)
        case _ =>
      }
  }
}

object RegisteredMoveEngineActor {
  def props(leaderboard: ActorRef): Props = {
    Props(classOf[RegisteredMoveEngineActor], leaderboard)
  }
}
