package actors

import akka.actor.{Actor, Props}
import models.MoveEngine

import scala.collection.immutable.SortedSet

class LeaderboardActor extends Actor {

  import LeaderboardActor._

  var entries: SortedSet[LeaderboardEntry] = SortedSet()

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
      println(s"entries: $entries")

    case GetLeadersRequest(count) =>
      val leaders = (entries take count).toSeq
      sender() ! GetLeadersResponse(leaders)
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

  case class LeaderboardEntry(username: String, numWon: Int, numLost: Int, numDrawn: Int) extends Ordered[LeaderboardEntry] {
    override def compare(that: LeaderboardEntry): Int = {
      val comparison1 = -(numWon compare that.numWon)
      val comparison2 = numLost compare that.numLost
      val comparison3 = -(numDrawn compare that.numDrawn)
      val comparison4 = username compare that.username
      if (comparison1 != 0) comparison1
      else {
        if (comparison2 != 0) comparison2
        else {
          if (comparison3 != 0) comparison3 else comparison4
        }
      }
    }
  }

  case class GameFinished(username: String, outcome: Int)
  case class GetLeadersRequest(count: Int)
  case class GetLeadersResponse(leaders: Seq[LeaderboardEntry])

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
