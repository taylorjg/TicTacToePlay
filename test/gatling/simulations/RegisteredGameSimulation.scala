// PLAY_SESSION
// 4c1c96a40c99ab29ec1b274fcfc54bba6718c4f1-username=Jon

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RegisteredGameSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:9000")
		.inferHtmlResources()
		.acceptHeader("*/*")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.8")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.116 Safari/537.36")

	val headers_0 = Map(
		"Cookie" -> "PLAY_SESSION=4c1c96a40c99ab29ec1b274fcfc54bba6718c4f1-username=Jon",
		"Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
		"Accept-Encoding" -> "gzip, deflate, sdch",
		"Upgrade-Insecure-Requests" -> "1")

	val headers_1 = Map(
		"Cookie" -> "PLAY_SESSION=4c1c96a40c99ab29ec1b274fcfc54bba6718c4f1-username=Jon",
		"Content-Type" -> "application/json",
		"Origin" -> "http://localhost:9000",
		"X-Requested-With" -> "XMLHttpRequest")



	val scn = scenario("RegisteredGameSimulation")
		.exec(http("request_0")
			.get("/registeredGame")
			.headers(headers_0))
		.pause(4)
		.exec(http("request_1")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(RawFileBody("RegisteredGameSimulation_0001_request.txt"))
			.resources(http("request_2")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(RawFileBody("RegisteredGameSimulation_0002_request.txt"))))
		.pause(32)
		.exec(http("request_3")
			.post("/api/computerMove")
			.headers(headers_1)
			.body(RawFileBody("RegisteredGameSimulation_0003_request.txt")))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}
