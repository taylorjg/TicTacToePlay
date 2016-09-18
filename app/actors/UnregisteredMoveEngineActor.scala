package actors

import akka.actor.{Actor, Props}
import models.{GameState, MoveEngine}

class UnregisteredMoveEngineActor extends Actor {

  import actors.UnregisteredMoveEngineActor._

  override def receive: Receive = {
    case UnregisteredGameMove(oldState) =>
      val newState = MoveEngine.computerMove(oldState)
      sender ! newState
  }
}

object UnregisteredMoveEngineActor {

  case class UnregisteredGameMove(oldState: GameState)

  def props: Props = Props(new UnregisteredMoveEngineActor)
}
