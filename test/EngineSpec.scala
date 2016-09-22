import models.{GameState, MoveEngine}
import models.Outcome._
import org.scalatest._

class EngineSpec extends FlatSpec with Matchers {

  private val PLAYER1_PIECE = 'X'
  private val PLAYER2_PIECE = 'O'

  private def gameStateWithBoard(board: String): GameState =
    GameState(board, PLAYER1_PIECE, PLAYER2_PIECE, None, None)

  "computerMove" should "make the winning move when possible" in {
    val oldState = gameStateWithBoard(
      "XOX" +
      "---" +
      "-O-")
    val newState = MoveEngine.computerMove(oldState)
    newState should be (GameState(
      "XOX" +
      "-O-" +
      "-O-",
      PLAYER1_PIECE, PLAYER2_PIECE, Some(Player2Win), Some(List(1, 4, 7))))
  }
}
