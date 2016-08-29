package formatters

import models.{GameState, LeaderboardEntry}
import play.api.data.validation.ValidationError
import play.api.libs.json._

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

  implicit val leaderboardEntryReads: Reads[LeaderboardEntry] = Json.reads[LeaderboardEntry]
  implicit val leaderboardEntryWrites: Writes[LeaderboardEntry] = Json.writes[LeaderboardEntry]
}
