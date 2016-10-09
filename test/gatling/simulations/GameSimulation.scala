package gatling.simulations

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import scala.language.postfixOps

class GameSimulation(registeredGame: Boolean) extends Simulation {

  private implicit class ScenarioBuilderExtensions(sb: ScenarioBuilder) {

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

    private final val GAME_PAGE_URL = if (registeredGame) "/registeredGame" else "/unregisteredGame"

    def loadGamePage(): ScenarioBuilder = {
      sb.exec(http("loadPage").get(GAME_PAGE_URL))
    }

    def randomPause(): ScenarioBuilder =
      sb.pause(random.nextInt(3))
  }

  private implicit class ChainBuilderExtensions(cb: ChainBuilder) {

    private val postHeaders = Map("Content-Type" -> "application/json")

    private def saveBoard() = jsonPath("$.board").saveAs("board")

    private def saveOutcome() = jsonPath("$.outcome").ofType[Int].optional.saveAs("outcome")

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

    def randomPause(): ChainBuilder =
      cb.pause(random.nextInt(3))
  }

  private def updateSessionValue[A](key: String)(f: A => A): Expression[Session] =
    session => session.set(key, f(session(key).as[A]))

  private val random = scala.util.Random
  private final val INITIAL_BOARD = "---------"

  private def coinTossIsHeads(): Boolean = {
    // [0..4] = heads
    // [5..9] = tails
    random.nextInt(10) <= 4
  }

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

  private val initialiseSessionValues: Expression[Session] = session =>
    session
      .set("board", INITIAL_BOARD)
      .set("moveNumber", 1)
      .remove("outcome")

  private val gameIsNotOver: Expression[Boolean] = "${outcome.isUndefined()}"

  private val updateBoard: Expression[Session] = updateSessionValue("board")(makeHumanMove)

  private val feeder = Iterator.from(1).map(i => Map("n" -> i))

  val numUsers = Integer.getInteger("numUsers", 1)
  val ramp = Integer.getInteger("ramp", 1)
  println(s"numUsers: $numUsers")
  println(s"ramp: $ramp")

  private val scn = scenario("GameSimulation")
    .feed(feeder)
    .doRegistration("testuser${n}")
    .randomPause()
    .loadGamePage()
    .randomPause()
    .repeat(5) {
      exec(initialiseSessionValues)
        .asLongAs(gameIsNotOver) {
          exec(updateBoard)
            .makeComputerMove()
            .incrementMoveNumber()
            .randomPause()
        }
    }

  private val httpProtocol = http
    .baseURL("http://localhost:9000")
    .inferHtmlResources()

  setUp(scn.inject(rampUsers(numUsers) over (ramp seconds))).protocols(httpProtocol)
}
