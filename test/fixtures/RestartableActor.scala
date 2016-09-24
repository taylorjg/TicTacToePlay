package fixtures

import akka.persistence.PersistentActor

trait RestartableActor extends PersistentActor {

  import fixtures.RestartableActor._

  abstract override def receiveCommand = super.receiveCommand orElse {
    case RestartActor => throw RestartActorException
  }
}

object RestartableActor {

  case object RestartActor

  private object RestartActorException extends Exception

}
