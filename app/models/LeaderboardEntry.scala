package models

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
