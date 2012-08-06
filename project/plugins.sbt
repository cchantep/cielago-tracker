// Comment to get more information during initialization
logLevel := Level.Warn

// Plugins resolvers
resolvers += "typesafe" at "http://repo.typesafe.com/typesafe/releases/"

// Atewaza
resolvers += "cchantep" at "https://raw.github.com/cchantep/tatami/develop/"

libraryDependencies += "cchantep" %% "atewaza" % "1.0.0"

// Use the Play sbt plugin for Play projects
//addSbtPlugin("play" % "sbt-plugin" % "2.1-SNAPSHOT")
addSbtPlugin("play" % "sbt-plugin" % "2.1-07132012")

//addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.0.10")
