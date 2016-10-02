package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ScenarioBuilder

class UnregisteredGameSimulation extends Simulation {

  implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {
    def dumpSession(): ScenarioBuilder = {
      sb.exec(session => {
        println(s"session: $session")
        session
      })
    }
  }

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
      .body(StringBody("""{"board":"X--------","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove2")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"X---X--O-","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove3")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"X-X-X--OO","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
