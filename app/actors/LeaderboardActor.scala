package actors

import actors.LeaderboardUpdatesActor.SubscribeForLeaderboardUpdates
import akka.actor.{ActorRef, Props, Terminated}
import akka.pattern.ask
import akka.persistence.{PersistentActor, RecoveryCompleted}
import defaults.Defaults._
import models.{LeaderboardEntry, MoveEngine}
import play.api.Logger

import scala.collection.immutable.SortedSet

class LeaderboardActor extends PersistentActor {

  import LeaderboardActor._

  implicit val ex = context.dispatcher

  var entries: SortedSet[LeaderboardEntry] = SortedSet()
  var subscriptions: Set[ActorRef] = Set()

  override def persistenceId: String = self.path.name

  override def receiveRecover: Receive = {
    case event: GameFinished => {
      Logger.info(s"[LeaderboardActor.receiveRecover] event: $event")
      entries = applyEvent(event)
    }
    case RecoveryCompleted => Logger.info("[LeaderboardActor.receiveRecover] RecoveryCompleted")
  }

  override def receiveCommand: Receive = {

    case command: GameFinished =>
      persist(command) { event =>
        entries = applyEvent(event)
        val future = (self ? GetLeadersRequest).mapTo[GetLeadersResponse]
        future map { response =>
          subscriptions foreach {
            _ ! response
          }
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

  private def wonDrawnLostFrom(outcome: Int): (Int, Int, Int) = {
    def oneIfOutcomeIs(outcomeType: Int): Int = if (outcome == outcomeType) 1 else 0
    val won = oneIfOutcomeIs(MoveEngine.OUTCOME_PLAYER1_WIN)
    val drawn = oneIfOutcomeIs(MoveEngine.OUTCOME_DRAW)
    val lost = oneIfOutcomeIs(MoveEngine.OUTCOME_PLAYER2_WIN)
    (won, drawn, lost)
  }

  private def applyEvent: PartialFunction[GameFinished, SortedSet[LeaderboardEntry]] = {
    case GameFinished(username, outcome) =>
      val (won, drawn, lost) = wonDrawnLostFrom(outcome)
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

  case class GameFinished(username: String, outcome: Int)

  case object GetLeadersRequest

  case class GetLeadersResponse(leaders: Seq[LeaderboardEntry])

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
