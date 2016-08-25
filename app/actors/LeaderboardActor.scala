package actors

import akka.actor.{Actor, Props}
import models.GameState

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
    case GameFinished(gameState) => {
      val oldEntry = entries find { e => e.username == gameState.username.get }
      val newEntry = oldEntry.fold(
        LeaderboardEntry(
          gameState.username.get,
          if (gameState.outcome.get == 1) 1 else 0,
          if (gameState.outcome.get == 2) 1 else 0,
          if (gameState.outcome.get == 3) 1 else 0)
      ) { e => {
        entries -= e
        e.copy(
          numWon = e.numWon + (if (gameState.outcome.get == 1) 1 else 0),
          numLost = e.numLost + (if (gameState.outcome.get == 2) 1 else 0),
          numDrawn = e.numDrawn + (if (gameState.outcome.get == 3) 1 else 0))
      }
      }
      entries += newEntry
      println(s"entruies: $entries")
    }
  }
}

object LeaderboardActor {

  case class GameFinished(gameState: GameState)

  def props: Props = {
    Props(classOf[LeaderboardActor])
  }
}
