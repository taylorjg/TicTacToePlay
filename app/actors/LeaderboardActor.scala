package actors

import actors.LeaderboardUpdatesActor.SubscribeForLeaderboardUpdates
import akka.actor.{ActorRef, Props, Terminated}
import akka.pattern.ask
import akka.persistence.{PersistentActor, RecoveryCompleted}
import defaults.Defaults._
import models.LeaderboardEntry
import models.Outcome._
import play.api.Logger

import scala.collection.immutable.SortedSet

class LeaderboardActor extends PersistentActor {

  import LeaderboardActor._

  implicit val ex = context.dispatcher

  var entries: SortedSet[LeaderboardEntry] = SortedSet()
  var subscriptions: Set[ActorRef] = Set()

  override def persistenceId: String = self.path.name

  override def receiveRecover: Receive = {
    case event: GameFinished =>
      Logger.info(s"[LeaderboardActor.receiveRecover] event: $event")
      entries = applyEvent(event)
    case RecoveryCompleted => Logger.info("[LeaderboardActor.receiveRecover] RecoveryCompleted")
  }

  override def receiveCommand: Receive = {

    case command: GameFinished =>
      persist(command) { event =>
        entries = applyEvent(event)
        val future = (self ? GetLeadersRequest).mapTo[GetLeadersResponse]
        future map { response =>
          subscriptions foreach (_ ! response)
        }
      }

    case GetLeadersRequest =>
      val leaders = (entries take LEADERBOARD_SIZE).toSeq
      sender() ! GetLeadersResponse(leaders)

    case SubscribeForLeaderboardUpdates =>
      val subscriber = sender()
      context.watch(subscriber)
      subscriptions += subscriber

    case Terminated(subscriber) =>
      subscriptions -= subscriber
  }

  private def wonDrawnLostFrom(outcome: Outcome): (Int, Int, Int) = {
    def oneIfOutcomeIs(specificOutcome: Outcome): Int = if (outcome == specificOutcome) 1 else 0
    val won = oneIfOutcomeIs(Player1Win)
    val drawn = oneIfOutcomeIs(Draw)
    val lost = oneIfOutcomeIs(Player2Win)
    (won, drawn, lost)
  }

  private def applyEvent: PartialFunction[GameFinished, SortedSet[LeaderboardEntry]] = {
    case GameFinished(username, outcome) =>
      val (won, drawn, lost) = wonDrawnLostFrom(outcome)
      require(won + drawn + lost == 1)
      val oldEntryOption = entries find (_.username == username)
      val newEntry = oldEntryOption match {
        case Some(oldEntry) =>
          oldEntry.copy(
            numWon = oldEntry.numWon + won,
            numDrawn = oldEntry.numDrawn + drawn,
            numLost = oldEntry.numLost + lost)
        case None =>
          LeaderboardEntry(username, won, drawn, lost)
      }
      entries -- oldEntryOption.toList + newEntry
  }
}

object LeaderboardActor {

  case class GameFinished(username: String, outcome: Outcome)

  case object GetLeadersRequest

  case class GetLeadersResponse(leaders: Seq[LeaderboardEntry])

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
