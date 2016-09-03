package models

case class LeaderboardEntry(username: String, numWon: Int, numDrawn: Int, numLost: Int) extends Ordered[LeaderboardEntry] {
  override def compare(that: LeaderboardEntry): Int = {
    def calcScore(le: LeaderboardEntry): Int = le.numWon * 3 + le.numDrawn
    val thisScore = calcScore(this)
    val thatScore = calcScore(that)
    val comparison1 = -(thisScore compare thatScore)
    val comparison2 = username compare that.username
    if (comparison1 != 0) comparison1 else comparison2
  }
}
