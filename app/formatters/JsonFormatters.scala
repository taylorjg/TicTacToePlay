package formatters

import models.Outcome.{Outcome, _}
import models.{GameState, LeaderboardEntry, User}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object JsonFormatters {

  implicit object CharReads extends Reads[Char] {
    def reads(json: JsValue): JsResult[Char] = json match {
      case JsString(s) if s.length == 1 => JsSuccess(s.head)
      case _ => JsError("expected a string of length 1")
    }
  }

  implicit object CharWrites extends Writes[Char] {
    def writes(o: Char): JsValue = JsString(o.toString)
  }

  implicit object OutcomeReads extends Reads[Outcome] {
    def reads(json: JsValue): JsResult[Outcome] = json match {
      case JsNumber(n) if n == 1 => JsSuccess(Player1Win)
      case JsNumber(n) if n == 2 => JsSuccess(Player2Win)
      case JsNumber(n) if n == 3 => JsSuccess(Draw)
      case _ => JsError("expected 1|2|3")
    }
  }

  implicit object OutcomeWrites extends Writes[Outcome] {
    def writes(o: Outcome): JsValue = o match {
      case Player1Win => JsNumber(1)
      case Player2Win => JsNumber(2)
      case Draw => JsNumber(3)
    }
  }

  implicit val gameStateReads: Reads[GameState] = Json.reads[GameState]
  implicit val gameStateWrites: Writes[GameState] = Json.writes[GameState]

  implicit val leaderboardEntryReads: Reads[LeaderboardEntry] = (
    (JsPath \ "username").read[String] and
    (JsPath \ "numWon").read[Int] and
    (JsPath \ "numDrawn").read[Int] and
    (JsPath \ "numLost").read[Int]
    )(LeaderboardEntry.apply _)
  implicit val leaderboardEntryWrites: Writes[LeaderboardEntry] = (
    (JsPath \ "username").write[String] and
    (JsPath \ "numWon").write[Int] and
    (JsPath \ "numDrawn").write[Int] and
    (JsPath \ "numLost").write[Int] and
    (JsPath \ "played").write[Int] and
    (JsPath \ "points").write[Int]
    )(le => (le.username, le.numWon, le.numDrawn, le.numLost, le.played, le.points))

  implicit val userReads: Reads[User] = Json.reads[User]
  implicit val userWrites: Writes[User] = Json.writes[User]
}
