package modules

import akka.actor.{Actor, Props}
import models.{GameState, MoveEngine}

class MoveEngineActor extends Actor {
  override def receive: Receive = {
    case oldState: GameState => sender ! MoveEngine.computerMove(oldState)
  }
}

object MoveEngineActor {
  def props: Props = {
    Props(classOf[MoveEngineActor])
  }
}
