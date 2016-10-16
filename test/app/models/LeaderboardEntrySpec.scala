package app.models

import org.scalatest._

class LeaderboardEntrySpec extends FlatSpec with Matchers {

  import models.LeaderboardEntry
  import models.LeaderboardEntry._
  import scala.collection.immutable.SortedSet

  it should "calculate the number of points correctly" in {
    val le = LeaderboardEntry("dont-core", 1, 2, 3)
    le.points should be(5)
  }

  it should "calculate the number of games played correctly" in {
    val le = LeaderboardEntry("dont-core", 1, 2, 3)
    le.played should be(6)
  }

  it should "order entries correctly when points scored are different" in {
    val le1 = LeaderboardEntry("dont-care-1", 2, 2, 2)
    val le2 = LeaderboardEntry("dont-care-2", 2, 1, 2)
    val les = SortedSet(le2, le1)
    les.toList should be(List(le1, le2))
  }

  it should "order entries correctly when points scored are equal" in {
    val le1 = LeaderboardEntry("user-abc", 2, 2, 2)
    val le2 = LeaderboardEntry("user-def", 2, 2, 2)
    val les = SortedSet(le2, le1)
    les.toList should be(List(le1, le2))
  }
}
