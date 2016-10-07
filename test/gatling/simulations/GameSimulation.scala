package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._

import scala.concurrent.duration._
import scala.language.postfixOps

class GameSimulation(registeredGame: Boolean) extends Simulation {

  val pageURL = if (registeredGame) "/registeredGame" else "/unregisteredGame"

  private implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {

    def loadPage(): ScenarioBuilder = {
      sb.exec(http("loadPage").get(pageURL))
    }

    def doRegistration(username: String): ScenarioBuilder = {
      if (registeredGame) {
        sb
          .exec(http("loadRegistrationPage")
            .get("/registration"))
          .exec(http("register")
            .post("/registration")
            .formParam("username", username)
            .formParam("password", username)
            .formParam("password2", username))
      }
      else sb
    }
  }

  private implicit class ChainBuilderExtensions(cb: ChainBuilder) {

    def makeComputerMove(): ChainBuilder = {
      cb
        .exec(http("computerMove-${moveNumber}")
          .post("/api/computerMove")
          .headers(postHeaders)
          .body(StringBody("""{"board":"${board}","player1Piece":"X","player2Piece":"O"}"""))
          .check(saveBoard(), saveOutcome())
        )
    }

    def incrementMoveNumber(): ChainBuilder =
      cb.exec(updateSessionValue[Int]("moveNumber")(_ + 1))
  }

  private def updateSessionValue[A](key: String)(f: A => A): Expression[Session] =
    session => session.set(key, f(session(key).as[A]))

  private val random = scala.util.Random

  private def saveBoard() = jsonPath("$.board").saveAs("board")

  private def saveOutcome() = jsonPath("$.outcome").ofType[Int].optional.saveAs("outcome")

  private def coinTossIsHeads(): Boolean = {
    // [0..4] = heads
    // [5..9] = tails
    random.nextInt(10) <= 4
  }

  private final val INITIAL_BOARD = "---------"

  private def makeHumanMove(board: String): String = {
    if (board == INITIAL_BOARD && coinTossIsHeads()) {
      board
    }
    else {
      val choices = board.zipWithIndex.collect { case (ch, index) if ch == '-' => index }
      val choice = choices(random.nextInt(choices.length))
      board.updated(choice, 'X')
    }
  }

  private val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  private val postHeaders = Map("Content-Type" -> "application/json")

  private val initialiseSessionValues: Expression[Session] = session =>
    session
      .set("board", INITIAL_BOARD)
      .set("moveNumber", 1)

  private val gameIsNotOver: Expression[Boolean] = session =>
    session("outcome").asOption[Int].isEmpty

  private val scn = scenario("GameSimulation")
    .loadPage()
    .doRegistration("testuser1")
    .exec(initialiseSessionValues)
    .asLongAs(gameIsNotOver) {
      exec(updateSessionValue("board")(makeHumanMove))
        .makeComputerMove()
        .incrementMoveNumber()
        .pause(1)
    }

  //setUp(scn.inject(rampUsers(200) over (20 seconds))).protocols(httpProtocol)
  setUp(scn.inject(rampUsers(1) over (1 seconds))).protocols(httpProtocol)
}
