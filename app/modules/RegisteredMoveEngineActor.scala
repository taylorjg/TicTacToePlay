package modules

import akka.actor.{Actor, Props}
import models.{GameState, MoveEngine}
import play.api.Logger

class RegisteredMoveEngineActor extends Actor {
  Logger.info(s"MoveEngineActor ${context.self.path}")
  override def receive: Receive = {
    case oldState: GameState => sender ! MoveEngine.computerMove(oldState)
  }
}

object RegisteredMoveEngineActor {
  def props: Props = {
    Props(classOf[RegisteredMoveEngineActor])
  }
}
