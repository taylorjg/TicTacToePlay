import io.gatling.core.Predef._
import io.gatling.http.Predef._

class UnregisteredGameSimulation extends Simulation {

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
	  .exec(session => {
			println(s"session: $session")
			session
		})
    .pause(1)
    .exec(http("humanMove2")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"X---X--O-","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .exec(session => {
      println(s"session: $session")
      session
    })
    .pause(1)
    .exec(http("humanMove3")
      .post("/api/computerMove")
      .headers(postHeaders)
      .body(StringBody("""{"board":"X-X-X--OO","player1Piece":"X","player2Piece":"O"}"""))
      .check(jsonPath("$.board").saveAs("board")))
    .exec(session => {
      println(s"session: $session")
      session
    })

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
