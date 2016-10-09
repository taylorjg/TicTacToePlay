package modules

import java.net.URI

import play.api.inject.guice.{GuiceApplicationBuilder, GuiceApplicationLoader}
import play.api.{ApplicationLoader, Configuration}

class CustomApplicationLoader extends GuiceApplicationLoader() {

  override def builder(context: ApplicationLoader.Context): GuiceApplicationBuilder = {

    val databaseUrl = context.initialConfiguration.getString("app.databaseUrl")
    val extraConfiguration = databaseUrl.fold(Configuration())(parseDatabaseUrl)

    initialBuilder
      .in(context.environment)
      .loadConfig(context.initialConfiguration ++ extraConfiguration)
      .overrides(overrides(context): _*)
  }

  private val DEFAULT_POSTGRES_PORT = 5432

  private def parseDatabaseUrl(databaseUrl: String): Configuration = {

    val uri = new URI(databaseUrl)
    val bits = uri.getUserInfo.split(":").lift
    val user = bits(0).getOrElse("")
    val password = bits(1).getOrElse("")
    val port = if (uri.getPort != -1) uri.getPort else DEFAULT_POSTGRES_PORT
    val url = s"jdbc:postgresql://${uri.getHost}:$port${uri.getPath}"

    Configuration(
      "akka-persistence-sql-async.user" -> user,
      "akka-persistence-sql-async.password" -> password,
      "akka-persistence-sql-async.url" -> url
    )
  }
}
