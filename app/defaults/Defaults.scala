package defaults

import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

object Defaults {
  implicit final val ASK_TIMEOUT = Timeout(5 seconds)
  final val LEADERBOARD_SIZE = 10
}
