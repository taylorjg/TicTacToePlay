package models

case class GameState(username: Option[String],
                     board: String,
                     player1Piece: Char,
                     player2Piece: Char,
                     outcome: Option[Int],
                     winningLine: Option[List[Int]]) {
}
