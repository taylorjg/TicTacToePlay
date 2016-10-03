package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

class DynamicGameSimulation extends Simulation {

  implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {

    def loadUnregisteredGamePage(): ScenarioBuilder = {
      sb
        .exec(http("loadUnregisteredGamePage")
          .get("/unregisteredGame"))
    }

    def makeHumanMove(board: String): ScenarioBuilder = {
      sb
        .pause(1)
        .exec(http(s"humanMove-$board")
          .post("/api/computerMove")
          .headers(postHeaders)
          .body(StringBody(makeGameStateJson(board)))
          .check(saveBoard(), saveOutcome()))
        .dumpSession()
    }

    def dumpSession(): ScenarioBuilder = {
      sb.exec(session => {
        println(s"[session] board: ${session("board").asOption[String]}; outcome: ${session("outcome").asOption[String]}")
        session
      })
    }
  }

  private def saveBoard() = jsonPath("$.board").saveAs("board")

  private def saveOutcome() = jsonPath("$.outcome").optional.saveAs("outcome")

  private def makeGameStateJson(board: String) =
    s"""{"board":"$board","player1Piece":"X","player2Piece":"O"}"""

  val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  val postHeaders = Map("Content-Type" -> "application/json")

  val scn = scenario("DynamicGameSimulation")
    .loadUnregisteredGamePage()
    .exec(session => session.set("board", "---------").set("moveNumber", 1))
    .asLongAs(session => session("outcome").asOption[Int].isEmpty) {
      exec(http("humanMove-${moveNumber}")
        .post("/api/computerMove")
        .headers(postHeaders)
        .body(StringBody(makeGameStateJson("${board}")))
        .check(saveBoard(), saveOutcome()))
        .exec(session => session.set("moveNumber", session("moveNumber").as[Int] + 1))
        .pause(1)
    }

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
