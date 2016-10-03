package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._

class DynamicGameSimulation extends Simulation {

  implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {
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

  val scn = scenario("UnregisteredGameSimulation")
    .exec(http("loadPage")
      .get("/unregisteredGame")
    )
    .pause(1)
    .exec(http("humanMove1")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody(makeGameStateJson("X--------")))
      .check(saveBoard(), saveOutcome()))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove2")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody(makeGameStateJson("X---X--O-")))
      .check(saveBoard(), saveOutcome()))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove3")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody(makeGameStateJson("X-X-X--OO")))
      .check(saveBoard(), saveOutcome()))
    .dumpSession()

  setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
