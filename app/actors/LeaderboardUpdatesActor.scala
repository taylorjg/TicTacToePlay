package actors

import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json
import utils.Utils

class LeaderboardUpdatesActor(mainActor: ActorRef, out: ActorRef) extends Actor {

  import actors.LeaderboardActor.GetLeadersResponse
  import actors.LeaderboardUpdatesActor._
  import formatters.JsonFormatters._

  mainActor ! SubscribeForLeaderboardUpdates

  override def receive: Receive = {
    case GetLeadersResponse(leaders) =>
      val clippedLeaders = leaders map (le => le.copy(username = Utils.userDisplayName(le.username).toString()))
      out ! Json.toJson(clippedLeaders)
  }
}

object LeaderboardUpdatesActor {

  case object SubscribeForLeaderboardUpdates

  def props(mainActor: ActorRef, out: ActorRef): Props = {
    Props(classOf[LeaderboardUpdatesActor], mainActor, out)
  }
}
