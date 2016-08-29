package defaults

import akka.util.Timeout
import scala.concurrent.duration._

object Defaults {
  implicit val ASK_TIMEOUT = Timeout(2.seconds)
  val LEADERBOARD_SIZE = 10
}
