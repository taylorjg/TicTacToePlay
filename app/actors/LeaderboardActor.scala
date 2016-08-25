package actors

import akka.actor.{Actor, Props}
import models.GameState

import scala.collection.immutable.SortedSet

class LeaderboardActor extends Actor {

  case class LeaderboardEntry(username: String, numWon: Int, numLost: Int, numDrawn: Int) extends Ordered[LeaderboardEntry] {
    override def compare(that: LeaderboardEntry): Int = numWon - that.numWon
  }

  var sortedSet: SortedSet[LeaderboardEntry] = SortedSet()

  import LeaderboardActor._

  override def receive: Receive = {
    case GameFinished(gameState) => {
      val oldEntry = sortedSet find { e => e.username == gameState.username.get }
      val newEntry = oldEntry.fold(
        LeaderboardEntry(
          gameState.username.get,
          if (gameState.outcome.get == 1) 1 else 0,
          if (gameState.outcome.get == 2) 1 else 0,
          if (gameState.outcome.get == 3) 1 else 0)
      ) { e => {
        println(s"sortedSet (before removal): $sortedSet")
        sortedSet = sortedSet - e
        println(s"sortedSet (after removal): $sortedSet")
        e.copy(
          numWon = e.numWon + (if (gameState.outcome.get == 1) 1 else 0),
          numLost = e.numLost + (if (gameState.outcome.get == 2) 1 else 0),
          numDrawn = e.numDrawn + (if (gameState.outcome.get == 3) 1 else 0))
      }
      }
      println(s"sortedSet (before addition): $sortedSet")
      sortedSet = sortedSet + newEntry
      println(s"sortedSet (after addition): $sortedSet")
    }
  }
}

object LeaderboardActor {

  case class GameFinished(gameState: GameState)

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
