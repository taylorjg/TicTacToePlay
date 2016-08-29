package actors

import actors.LeaderboardActor.GetLeadersResponse
import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json

class LeaderboardUpdatesActor(mainActor: ActorRef, out: ActorRef) extends Actor {

  import actors.LeaderboardUpdatesActor._
  import formatters.JsonFormatters._

  mainActor ! SubscribeForLeaderboardUpdates

  override def receive: Receive = {
    case GetLeadersResponse(leaders) =>
      out ! Json.toJson(leaders)
  }
}

object LeaderboardUpdatesActor {

  case object SubscribeForLeaderboardUpdates

  def props(mainActor: ActorRef, out: ActorRef): Props = {
    Props(classOf[LeaderboardUpdatesActor], mainActor, out)
  }
}
