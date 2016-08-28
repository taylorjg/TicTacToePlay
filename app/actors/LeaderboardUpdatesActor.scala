package actors

import actors.LeaderboardActor.GetLeadersResponse
import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json

class LeaderboardUpdatesActor(mainActor: ActorRef, out: ActorRef) extends Actor {

  import actors.LeaderboardUpdatesActor._
  import controllers.JsonFormatters._

  mainActor ! SubscribeForLeaderboardUpdates(self)

  override def receive: Receive = {
    case GetLeadersResponse(leaders) =>
      out ! Json.toJson(leaders)
  }
}

object LeaderboardUpdatesActor {

  case class SubscribeForLeaderboardUpdates(out: ActorRef)

  def props(mainActor: ActorRef, out: ActorRef): Props = {
    Props(classOf[LeaderboardUpdatesActor], mainActor, out)
  }
}
