package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

class DynamicGameSimulation extends Simulation {

  private implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {

    def loadUnregisteredGamePage(): ScenarioBuilder =
      sb
        .exec(http("loadUnregisteredGamePage")
          .get("/unregisteredGame"))

    def dumpSession(): ScenarioBuilder =
      sb.exec(session => {
        println(s"[session] board: ${session("board").asOption[String]}; outcome: ${session("outcome").asOption[Int]}")
        session
      })

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

  private def makeRandomMove(board: String): String = {
    if (board == "---------" && coinTossIsHeads()) {
      // computer to move first
      board
    }
    else {
      // human to move first
      val emptyLocationIndices = board.zipWithIndex.collect { case (ch, index) if ch == '-' => index }
      val randomChoice = random.nextInt(emptyLocationIndices.indices.length)
      val emptyLocationIndex = emptyLocationIndices(randomChoice)
      board.updated(emptyLocationIndex, 'X')
    }
  }

  private val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  private val postHeaders = Map("Content-Type" -> "application/json")

  private val initialiseSessionValues: Expression[Session] = session =>
    session
      .set("board", "---------")
      .set("moveNumber", 1)

  private val updateSessionBoardValueWithHumanMove: Expression[Session] = session =>
    session.set("board", makeRandomMove(session("board").as[String]))

  val scn = scenario("DynamicGameSimulation")
    .loadUnregisteredGamePage()
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

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
