// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.4")

// web plugins

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.2")

addSbtPlugin("io.teamscala.sbt" % "sbt-babel" % "1.0.5")

addSbtPlugin("com.heroku" % "sbt-heroku" % "1.0.1")

// https://github.com/zenato/sbt-babel/issues/2
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
