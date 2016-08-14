package models

case class GameState(board: String,
                     player1Piece: String,
                     player2Piece: String,
                     outcome: Option[Int],
                     winningLine: Option[List[Int]]) {
}
