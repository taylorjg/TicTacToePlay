package actors

import akka.actor.{Actor, Props}

import scala.collection.immutable.SortedSet

class LeaderboardActor extends Actor {

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

  var entries: SortedSet[LeaderboardEntry] = SortedSet()

  import LeaderboardActor._

  override def receive: Receive = {
    case GameFinished(username, outcome) =>
      val (won, lost, drawn) = outcomeToValues(outcome)
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
  }

  private def outcomeToValues(outcome: Int): (Int, Int, Int) = {
    val won = if (outcome == 1) 1 else 0
    val lost = if (outcome == 2) 1 else 0
    val drawn = if (outcome == 3) 1 else 0
    (won, lost, drawn)
  }
}

object LeaderboardActor {

  case class GameFinished(username: String, outcome: Int)

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
