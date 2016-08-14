package models

object MoveEngine {

  val LINES = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8),
    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8),
    List(0, 4, 8),
    List(2, 4, 6)
  )

  val random = scala.util.Random

  def computerMove(oldState: GameState): GameState = {
    checkForWinOrDraw(oldState).getOrElse(makeRandomMove(oldState))
  }

  private def makeRandomMove(oldState: GameState): GameState = {
    val board = oldState.board
    val emptyLocations = board.indices.filter(isEmpty(oldState, _))
    val randomEmptyLocationIndex = random.nextInt(emptyLocations.indices.length)
    val randomEmptyLocation = emptyLocations(randomEmptyLocationIndex)
    val newState = oldState.copy(board = board.updated(randomEmptyLocation, oldState.player2Piece.charAt(0)))
    checkForWinOrDraw(newState).getOrElse(newState)
  }

  private def checkForWinOrDraw(state: GameState): Option[GameState] = {
    val winningLines = LINES map {
      line => {
        checkForWinningLine(state, line) match {
          case Some(player) => Some(state.copy(outcome = Some(player), winningLine = Some(line)))
          case _ => None
        }
      }
    }
    winningLines find {
      _.isDefined
    } getOrElse (checkForDraw(state))
  }

  private def checkForWinningLine(state: GameState, line: List[Int]): Option[Int] = {
    val PLAYER1_WINNING_LINE = List.fill(3)(state.player1Piece.charAt(0))
    val PLAYER2_WINNING_LINE = List.fill(3)(state.player2Piece.charAt(0))
    line map { state.board(_) } match {
      case `PLAYER1_WINNING_LINE` => Some(1)
      case `PLAYER2_WINNING_LINE` => Some(2)
      case _ => None
    }
  }

  private def checkForDraw(state: GameState): Option[GameState] = {
    if (state.board.indices.exists(isEmpty(state, _))) None
    else Some(state.copy(outcome = Some(3)))
  }

  private def isEmpty(state: GameState, index: Int): Boolean = {
    val board = state.board
    val piece = board(index)
    piece != state.player1Piece.charAt(0) && piece != state.player2Piece.charAt(0)
  }
}
