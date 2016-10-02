package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.structure.ScenarioBuilder

class RegisteredGameSimulation extends Simulation {

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

  val getHeaders = Map(
    "Cookie" -> "PLAY_SESSION=4c1c96a40c99ab29ec1b274fcfc54bba6718c4f1-username=Jon")

  val postHeaders = Map(
    "Cookie" -> "PLAY_SESSION=4c1c96a40c99ab29ec1b274fcfc54bba6718c4f1-username=Jon",
    "Content-Type" -> "application/json")

  val scn = scenario("RegisteredGameSimulation")
    .exec(http("loadPage")
      .get("/registeredGame")
      .headers(getHeaders))
    .pause(1)
    .exec(http("humanMove1")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"------X--","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove1")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"-OX---X--","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()
    .pause(1)
    .exec(http("humanMove1")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"-OX-O-X-X","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .dumpSession()

  setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}
