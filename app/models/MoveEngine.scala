package models

import models.Outcome._

object MoveEngine {

  def computerMove(state: GameState): GameState = {
    checkForWinOrDraw(state)
      .orElse(tryToWin(state))
      .orElse(tryToBlock(state))
      .getOrElse(makeRandomMove(state))
  }

  private val LINES = List(
    List(0, 1, 2),
    List(3, 4, 5),
    List(6, 7, 8),
    List(0, 3, 6),
    List(1, 4, 7),
    List(2, 5, 8),
    List(0, 4, 8),
    List(2, 4, 6)
  )

  private val random = scala.util.Random

  private def makeRandomMove(state: GameState): GameState = {
    val board = state.board
    val emptyLocations = board.indices.filter(isEmpty(state, _))
    val randomEmptyLocationIndex = random.nextInt(emptyLocations.indices.length)
    val randomEmptyLocation = emptyLocations(randomEmptyLocationIndex)
    val newState = state.copy(board = board.updated(randomEmptyLocation, state.player2Piece))
    checkForWinOrDraw(newState).getOrElse(newState)
  }

  private def tryToWin(state: GameState): Option[GameState] = {
    checkForLineWithTwoPiecesAndOneEmpty(
      state,
      state.player2Piece,
      (newBoard, line) => state.copy(board = newBoard, outcome = Some(Player2Win), winningLine = Some(line)))
  }

  private def tryToBlock(state: GameState): Option[GameState] = {
    val newState = checkForLineWithTwoPiecesAndOneEmpty(
      state,
      state.player1Piece,
      (newBoard, _) => state.copy(board = newBoard))
    newState flatMap checkForWinOrDraw orElse newState
  }

  private def checkForLineWithTwoPiecesAndOneEmpty(state: GameState,
                                                   givenPiece: Char,
                                                   buildNewState: (String, List[Int]) => GameState): Option[GameState] = {
    val computerMoves = LINES map {
      line => {
        val indicesWithGivenPiece = line filter (state.board(_) == givenPiece)
        val indicesWithEmptyCells = line filter (isEmpty(state, _))
        if (indicesWithGivenPiece.length == 2 && indicesWithEmptyCells.length == 1) {
          val newBoard = state.board.updated(indicesWithEmptyCells.head, state.player2Piece)
          Some(buildNewState(newBoard, line))
        }
        else None
      }
    }
    computerMoves find {
      _.isDefined
    } getOrElse None
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
    } getOrElse checkForDraw(state)
  }

  private def checkForWinningLine(state: GameState, line: List[Int]): Option[Outcome] = {
    val PLAYER1_WINNING_LINE = List.fill(3)(state.player1Piece)
    val PLAYER2_WINNING_LINE = List.fill(3)(state.player2Piece)
    line map {
      state.board(_)
    } match {
      case `PLAYER1_WINNING_LINE` => Some(Player1Win)
      case `PLAYER2_WINNING_LINE` => Some(Player2Win)
      case _ => None
    }
  }

  private def checkForDraw(state: GameState): Option[GameState] = {
    if (state.board.indices.exists(isEmpty(state, _))) None
    else Some(state.copy(outcome = Some(Draw)))
  }

  private def isEmpty(state: GameState, index: Int): Boolean = {
    val piece = state.board(index)
    piece != state.player1Piece && piece != state.player2Piece
  }
}
