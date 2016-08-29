package actors

import actors.LeaderboardUpdatesActor.SubscribeForLeaderboardUpdates
import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import defaults.Defaults._
import models.{LeaderboardEntry, MoveEngine}

import scala.collection.immutable.SortedSet

class LeaderboardActor extends Actor {

  import LeaderboardActor._

  implicit val ex = context.dispatcher

  var entries: SortedSet[LeaderboardEntry] = SortedSet()
  var subscriptions: Set[ActorRef] = Set()

  override def receive: Receive = {

    case GameFinished(username, outcome) =>
      val (won, lost, drawn) = wonLostDrawnFrom(outcome)
      entries find (_.username == username) match {
        case Some(oldEntry) =>
          entries -= oldEntry
          entries += oldEntry.copy(
            numWon = oldEntry.numWon + won,
            numLost = oldEntry.numLost + lost,
            numDrawn = oldEntry.numDrawn + drawn)
        case None =>
          entries += LeaderboardEntry(username, won, lost, drawn)
      }
      val future = (self ? GetLeadersRequest).mapTo[GetLeadersResponse]
      future map { msg =>
        subscriptions foreach { _ ! msg }
      }

    case GetLeadersRequest =>
      val leaders = (entries take LEADERBOARD_SIZE).toSeq
      sender() ! GetLeadersResponse(leaders)

    case SubscribeForLeaderboardUpdates =>
      subscriptions += sender()
  }

  private def wonLostDrawnFrom(outcome: Int): (Int, Int, Int) = {
    def oneIfOutcomeIs(outcomeType: Int): Int = if (outcome == outcomeType) 1 else 0
    val won = oneIfOutcomeIs(MoveEngine.OUTCOME_PLAYER1_WIN)
    val lost = oneIfOutcomeIs(MoveEngine.OUTCOME_PLAYER2_WIN)
    val drawn = oneIfOutcomeIs(MoveEngine.OUTCOME_DRAW)
    (won, lost, drawn)
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
