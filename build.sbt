name := """play-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

libraryDependencies += "com.okumin" %% "akka-persistence-sql-async" % "0.3.1"
libraryDependencies += "com.github.mauricio" %% "postgresql-async" % "0.2.16"
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "2.6"
libraryDependencies += filters

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// https://github.com/playframework/playframework/issues/4839
resolvers += Resolver.url("Typesafe Ivy releases", url("https://repo.typesafe.com/typesafe/ivy-releases"))(Resolver.ivyStylePatterns)

// http://stackoverflow.com/questions/20281554/sbt-error-when-forking-is-enabled
// https://github.com/typesafehub/activator/issues/1036
// fork in run := true

herokuAppName in Compile := "tictactoeplay"
