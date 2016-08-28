package controllers

import javax.inject._

import actors.LeaderboardActor._
import actors.LeaderboardUpdatesActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import models._
import play.api.Logger
import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.streams._
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

@Singleton
class ApiController @Inject()(@Named("mainActor") mainActor: ActorRef)(implicit system: ActorSystem, materializer: Materializer)
  extends Controller {

  import JsonFormatters._

  implicit val timeout = Timeout(2 seconds)

  def computerMove = Action.async(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"computerMove: oldState: $oldState")

    val future = (mainActor ? oldState).mapTo[GameState]
    future map { newState =>
      Logger.info(s"computerMove: newState: $newState")
      Ok(Json.toJson(newState))
    }
  }

  def getLeaderboard = Action.async { request =>
    val future = (mainActor ? GetLeadersRequest(10)).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(Json.toJson(getLeadersResponse.leaders))
    }
  }

  def leaderboardUpdates = WebSocket.accept[JsValue, JsValue] {
    request => ActorFlow.actorRef(out => LeaderboardUpdatesActor.props(mainActor, out))
  }
}

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
