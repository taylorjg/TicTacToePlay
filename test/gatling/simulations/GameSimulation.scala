package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import scala.concurrent.duration._

class GameSimulation(pageURL: String, playSession: Option[String] = None) extends Simulation {

  private implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {
    def loadPage(): ScenarioBuilder = {
      sb
        .exec(http("loadPage")
          .get(pageURL)
          .headers(getHeaders))
    }
  }

  private implicit class ChainBuilderExtensions(cb: ChainBuilder) {
    def incrementMoveNumber(): ChainBuilder = {
      cb
        .exec(session => session.set("moveNumber", session("moveNumber").as[Int] + 1))
    }
  }

  private val random = scala.util.Random

  private def saveBoard() = jsonPath("$.board").saveAs("board")

  private def saveOutcome() = jsonPath("$.outcome").ofType[Int].optional.saveAs("outcome")

  private def coinTossIsHeads(): Boolean = {
    // [0..4] = heads
    // [5..9] = tails
    random.nextInt(10) <= 4
  }

  private final val INITIAL_BOARD = "---------"

  private def makeRandomMove(board: String): String = {
    if (board == INITIAL_BOARD && coinTossIsHeads()) {
      board
    }
    else {
      val emptyLocationIndices = board.zipWithIndex.collect { case (ch, index) if ch == '-' => index }
      val randomChoice = random.nextInt(emptyLocationIndices.indices.length)
      val emptyLocationIndex = emptyLocationIndices(randomChoice)
      board.updated(emptyLocationIndex, 'X')
    }
  }

  private val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  private val cookieHeader: Map[String, String] = playSession match {
    case Some(value) => Map("Cookie" -> value)
    case None => Map()
  }

  private val getHeaders = cookieHeader

  private val postHeaders = Map("Content-Type" -> "application/json") ++ cookieHeader

  private val initialiseSessionValues: Expression[Session] = session =>
    session
      .set("board", INITIAL_BOARD)
      .set("moveNumber", 1)

  // TODO: extract a HOF to update a session value
  private val updateSessionBoardValueWithHumanMove: Expression[Session] = session =>
    session.set("board", makeRandomMove(session("board").as[String]))

  private val scn = scenario("DynamicGameSimulation")
    .loadPage()
    .exec(initialiseSessionValues)
    .asLongAs(session => session("outcome").asOption[Int].isEmpty) {
      exec(updateSessionBoardValueWithHumanMove)
        .exec(http("humanMove-${moveNumber}")
          .post("/api/computerMove")
          .headers(postHeaders)
          .body(StringBody("""{"board":"${board}","player1Piece":"X","player2Piece":"O"}"""))
          .check(saveBoard(), saveOutcome())
        )
        .incrementMoveNumber()
        .pause(1)
    }

  setUp(scn.inject(rampUsers(650) over (20 seconds))).protocols(httpProtocol)
}
