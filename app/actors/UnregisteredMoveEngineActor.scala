package actors

import akka.actor.{Actor, Props}
import models.{GameState, MoveEngine}

class UnregisteredMoveEngineActor extends Actor {
  override def receive: Receive = {
    case oldState: GameState => sender ! MoveEngine.computerMove(oldState)
  }
}

object UnregisteredMoveEngineActor {
  def props: Props = {
    Props(classOf[UnregisteredMoveEngineActor])
  }
}
