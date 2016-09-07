package actors

import akka.actor.{Actor, Props}

class MainActor extends Actor {

  import actors.LeaderboardActor.GetLeadersRequest
  import actors.LeaderboardUpdatesActor.SubscribeForLeaderboardUpdates
  import actors.UnregisteredMoveEngineActor.UnregisteredGameMove
  import actors.RegisteredMoveEngineActor.RegisteredGameMove
  import actors.UsersActor._

  val unregisteredMoveEngine = context.actorOf(UnregisteredMoveEngineActor.props, "UnregisteredMoveEngine")
  val leaderboard = context.actorOf(LeaderboardActor.props, "Leaderboard")
  val users = context.actorOf(UsersActor.props, "Users")
  val utf8 = java.nio.charset.StandardCharsets.UTF_8.name()

  override def receive: Receive = {

    case msg: UnregisteredGameMove => unregisteredMoveEngine forward msg

    case msg @ RegisteredGameMove(_, username) =>
      val encodedUsername = java.net.URLEncoder.encode(username, utf8)
      lazy val newMoveEngine = context.actorOf(RegisteredMoveEngineActor.props(leaderboard), encodedUsername)
      val moveEngine = context.child(encodedUsername) getOrElse newMoveEngine
      moveEngine forward msg

    case GetLeadersRequest => leaderboard forward GetLeadersRequest

    case SubscribeForLeaderboardUpdates => leaderboard forward SubscribeForLeaderboardUpdates

    case msg: RegisterUserRequest => users forward msg

    case msg: LoginRequest => users forward msg

    case msg: LookupUsernameRequest => users forward msg
  }
}

object MainActor {
  def props: Props = {
    Props(classOf[MainActor])
  }
}
