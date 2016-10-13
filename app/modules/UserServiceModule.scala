package modules

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import services.{UserService, UserServiceImpl}

class UserServiceModule extends AbstractModule with AkkaGuiceSupport {
  def configure(): Unit = {
    bind(classOf[UserService]).to(classOf[UserServiceImpl])
  }
}
