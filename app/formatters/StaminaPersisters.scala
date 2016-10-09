package formatters

import actors.LeaderboardActor.GameFinished
import fommil.sjs.FamilyFormats._
import models.{Outcome, User}
import spray.json.{DeserializationException, JsString, JsValue, JsonFormat}
import stamina.StaminaAkkaSerializer
import stamina.json._

object StaminaPersisters {

  private def jsonEnum[T <: Enumeration](enu: T) = new JsonFormat[T#Value] {
    def write(obj: T#Value) = JsString(obj.toString)
    def read(json: JsValue) = json match {
      case JsString(txt) => enu.withName(txt)
      case something => throw DeserializationException(s"Expected a value from enum $enu instead of $something")
    }
  }

  implicit val outcomeFormat = jsonEnum(Outcome)

  val v1GameFinishedPersister = persister[GameFinished]("game-finished")
  val v1UserPersister = persister[User]("user")
}

import formatters.StaminaPersisters._

class StaminaSerialiser extends StaminaAkkaSerializer(v1GameFinishedPersister, v1UserPersister)
