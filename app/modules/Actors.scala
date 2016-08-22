package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class ActorsModule extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bindActor[MoveEngineActor]("moveEngine")
  }
}
