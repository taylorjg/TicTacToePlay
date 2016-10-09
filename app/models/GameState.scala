package models

import models.Outcome.Outcome

case class GameState(board: String,
                     player1Piece: Char,
                     player2Piece: Char,
                     outcome: Option[Outcome],
                     winningLine: Option[List[Int]])
