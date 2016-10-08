package defaults

import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

object Defaults {
  implicit val ASK_TIMEOUT = Timeout(5 seconds)
  val LEADERBOARD_SIZE = 10
}
