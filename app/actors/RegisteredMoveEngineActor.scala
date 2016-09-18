package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{GameState, MoveEngine}

class RegisteredMoveEngineActor(leaderboard: ActorRef) extends Actor {

  import actors.RegisteredMoveEngineActor._
  import actors.LeaderboardActor._

  override def receive: Receive = {
    case RegisteredGameMove(oldState, username) =>
      val newState = MoveEngine.computerMove(oldState)
      sender ! newState
      newState.outcome match {
        case Some(outcome) => leaderboard ! GameFinished(username, outcome)
        case _ =>
      }
  }
}

object RegisteredMoveEngineActor {

  case class RegisteredGameMove(oldState: GameState, username: String)

  def props(leaderboard: ActorRef): Props = Props(new RegisteredMoveEngineActor(leaderboard))
}
