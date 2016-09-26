package fixtures

import akka.persistence.PersistentActor

trait RestartableActor extends PersistentActor {

  import fixtures.RestartableActor._

  abstract override def receiveCommand = super.receiveCommand orElse {
    case RestartActor => throw new RestartActorException
  }
}

object RestartableActor {

  case object RestartActor

  class RestartActorException extends Exception

}
