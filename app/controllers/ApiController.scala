package controllers

import javax.inject._

import actors.LeaderboardActor._
import actors.LeaderboardUpdatesActor
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import defaults.Defaults._
import models._
import play.api.Logger
import play.api.libs.json._
import play.api.libs.streams._
import play.api.mvc._
import play.api.routing._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class ApiController @Inject()(@Named("mainActor") mainActor: ActorRef)(implicit system: ActorSystem, materializer: Materializer)
  extends Controller {

  import formatters.JsonFormatters._
  import actors.UnregisteredMoveEngineActor.UnregisteredGameMove
  import actors.RegisteredMoveEngineActor.RegisteredGameMove

  def jsRoutes = Action { implicit request =>
    Ok(JavaScriptReverseRouter("jsRoutes")(routes.javascript.ApiController.leaderboardUpdates)).as("text/javascript")
  }

  def computerMove = Action.async(parse.json) { request =>

    val oldState = request.body.as[GameState]
    Logger.info(s"computerMove: oldState: $oldState")

    val msg = request.session.get("username") match {
      case Some(username) => RegisteredGameMove(oldState, username)
      case None => UnregisteredGameMove(oldState)
    }

    (mainActor ? msg).mapTo[GameState] map { newState =>
      Logger.info(s"computerMove: newState: $newState")
      Ok(Json.toJson(newState))
    }
  }

  def getLeaderboard = Action.async { request =>
    val future = (mainActor ? GetLeadersRequest).mapTo[GetLeadersResponse]
    future map { getLeadersResponse =>
      Ok(Json.toJson(getLeadersResponse.leaders))
    }
  }

  def leaderboardUpdates = WebSocket.accept[JsValue, JsValue] {
    request => ActorFlow.actorRef(out => LeaderboardUpdatesActor.props(mainActor, out))
  }
}
