package models

case class LeaderboardEntry(username: String, numWon: Int, numDrawn: Int, numLost: Int)

object LeaderboardEntry {

  implicit def ordering: Ordering[LeaderboardEntry] = new Ordering[LeaderboardEntry] {
    override def compare(x: LeaderboardEntry, y: LeaderboardEntry): Int = {
      val comparison1 = -(x.points compare y.points)
      val comparison2 = x.username compare y.username
      if (comparison1 != 0) comparison1 else comparison2
    }
  }

  implicit class LeaderboardEntryExtensions(le: LeaderboardEntry) {
    def played: Int = le.numWon + le.numDrawn + le.numLost
    def points: Int = le.numWon * 3 + le.numDrawn
  }
}
