package models

case class GameState(board: String,
                     player1Piece: Char,
                     player2Piece: Char,
                     outcome: Option[Int],
                     winningLine: Option[List[Int]]) {
}
