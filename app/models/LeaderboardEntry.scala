package models

case class LeaderboardEntry(username: String, numWon: Int, numDrawn: Int, numLost: Int) extends Ordered[LeaderboardEntry] {

  override def compare(that: LeaderboardEntry): Int = {
    val thisScore = this.points
    val thatScore = that.points
    val comparison1 = -(thisScore compare thatScore)
    val comparison2 = username compare that.username
    if (comparison1 != 0) comparison1 else comparison2
  }

  val played: Int = calcPlayed(this)
  val points: Int = calcPoints(this)

  private def calcPlayed(le: LeaderboardEntry): Int = le.numWon + le.numDrawn + le.numLost

  private def calcPoints(le: LeaderboardEntry): Int = le.numWon * 3 + le.numDrawn
}
