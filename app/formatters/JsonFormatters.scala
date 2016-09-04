package formatters

import models.{GameState, LeaderboardEntry}
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonFormatters {

  implicit object CharReads extends Reads[Char] {
    def reads(json: JsValue) = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.head)
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("expected a string of length 1"))))
    }
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char) = JsString(o.toString)
  }

  implicit val gameStateReads: Reads[GameState] = Json.reads[GameState]
  implicit val gameStateWrites: Writes[GameState] = Json.writes[GameState]

  implicit val leaderboardEntryWrites: Writes[LeaderboardEntry] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "numWon").write[Int] and
    (JsPath \ "numDrawn").write[Int] and
    (JsPath \ "numLost").write[Int] and
    (JsPath \ "played").write[Int] and
    (JsPath \ "points").write[Int]
    )(le => (le.username, le.numWon, le.numDrawn, le.numLost, le.played, le.points))
}
