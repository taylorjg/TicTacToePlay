import io.gatling.core.Predef._
import io.gatling.http.Predef._

class UnregisteredGameSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:9000")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.8")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36")

	val headers_0 = Map(
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, sdch",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Content-Type" -> "application/json",
		"Origin" -> "http://localhost:9000",
		"X-Requested-With" -> "XMLHttpRequest")

	val scn = scenario("UnregisteredGameSimulation")
		.exec(http("request_0")
			.get("/unregisteredGame")
			.headers(headers_0))
		.pause(2)
		.exec(http("request_1")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(StringBody("""{"board":"X--------","player1Piece":"X","player2Piece":"O"}"""))
			.resources(http("request_2")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(StringBody("""{"board":"X---X--O-","player1Piece":"X","player2Piece":"O"}""")),
            http("request_3")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(StringBody("""{"board":"X-X-X--OO","player1Piece":"X","player2Piece":"O"}"""))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
