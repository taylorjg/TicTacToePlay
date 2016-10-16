package models

case class LeaderboardEntry(username: String, numWon: Int, numDrawn: Int, numLost: Int)

object LeaderboardEntry {

  implicit def ordering: Ordering[LeaderboardEntry] = new Ordering[LeaderboardEntry] {
    private val comparePointsDescending = Ordering[Int].reverse.compare _
    override def compare(x: LeaderboardEntry, y: LeaderboardEntry): Int = {
      val pointsComparison = comparePointsDescending(x.points, y.points)
      if (pointsComparison != 0) pointsComparison
      else x.username compare y.username
    }
  }

  implicit class LeaderboardEntryExtensions(le: LeaderboardEntry) {
    def played: Int = le.numWon + le.numDrawn + le.numLost
    def points: Int = le.numWon * 3 + le.numDrawn
  }
}
