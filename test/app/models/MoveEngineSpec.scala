package app.models

import models.Outcome._
import models.Outcome.Outcome
import models.{GameState, MoveEngine}
import org.scalatest._

class MoveEngineSpec extends FlatSpec with Matchers {

  private val PLAYER1_PIECE = 'X'
  private val PLAYER2_PIECE = 'O'

  private def makeGameState(board: String,
                            outcome: Option[Outcome] = None,
                            winningLine: Option[List[Int]] = None): GameState =
    GameState(
      board.stripMargin.replaceAll("""\s""", ""),
      PLAYER1_PIECE,
      PLAYER2_PIECE,
      outcome,
      winningLine)

  private def checkMove(oldState: GameState, expectedNewState: GameState): Unit = {
    val actualNewState = MoveEngine.computerMove(oldState)
    actualNewState should be(expectedNewState)
  }

  it should "make the winning move when possible" in {
    checkMove(
      makeGameState(
        """
          | XOX
          | ---
          | -O-
        """),
      makeGameState(
        """
          | XOX
          | -O-
          | -O-
        """,
        Some(Player2Win),
        Some(List(1, 4, 7))))
  }

  it should "make the blocking move when necessary" in {
    checkMove(
      makeGameState(
        """
          | X-O
          | OX-
          | ---
        """),
      makeGameState(
        """
          | X-O
          | OX-
          | --O
        """))
  }

  it should "detect when the human player has already won" in {
    checkMove(
      makeGameState(
        """
          | X-X
          | OXO
          | X-O
        """),
      makeGameState(
        """
          | X-X
          | OXO
          | X-O
        """,
        Some(Player1Win),
        Some(List(2, 4, 6))))
  }

  it should "detect a draw when the human player went first" in {
    checkMove(
      makeGameState(
        """
          | OXO
          | OXX
          | XOX
        """),
      makeGameState(
        """
          | OXO
          | OXX
          | XOX
        """,
        Some(Draw)))
  }

  it should "detect a draw when the computer went first" in {
    checkMove(
      makeGameState(
        """
          | OX-
          | XOO
          | XOX
        """),
      makeGameState(
        """
          | OXO
          | XOO
          | XOX
        """,
        Some(Draw)))
  }

  it should "return a draw when the only possible computer move happens to be a blocking move" in {
    checkMove(
      makeGameState(
        """
          | XOX
          | OXO
          | -XO
        """),
      makeGameState(
        """
          | XOX
          | OXO
          | OXO
        """,
        Some(Draw)))
  }
}
