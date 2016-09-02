package modules

import java.net.URI

import play.api.{ApplicationLoader, Configuration, Logger}
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}

class CustomApplicationLoader extends GuiceApplicationLoader() {

  println("CustomApplicationLoader")

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {

    val databaseUrl = context.initialConfiguration.getString("app.databaseUrl")
    Logger.info(s"databaseUrl: $databaseUrl")

    val uri = new URI(databaseUrl.get)
    val bits = uri.getUserInfo.split(":")
    val numBits = bits.length
    val user = if (numBits >= 1) bits(0) else ""
    val password = if (numBits >= 2) bits(1) else ""
    val port = if (uri.getPort != -1) uri.getPort else 5432
    val url = s"jdbc:postgresql://${uri.getHost}:${port}${uri.getPath}"

    Logger.info(s"user: $user")
    Logger.info(s"password: $password")
    Logger.info(s"url: $url")

    val extra = Configuration(
      "akka-persistence-sql-async.user" -> user,
      "akka-persistence-sql-async.password" -> password,
      "akka-persistence-sql-async.url" -> url
    )

    initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration ++ extra)
      .overrides(overrides(context): _*)
  }
}
