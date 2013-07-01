// Comment to get more information during initialization
logLevel := Level.Warn

// Plugins resolvers
resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  "Applicius Snapshots" at "https://raw.github.com/applicius/mvn-repo/master/snapshots",
  Resolver.url("Applicius Ivy Snapshots", new URL("https://raw.github.com/applicius/mvn-repo/master/ivy-snapshots/"))(Resolver.ivyStylePatterns))

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.2-SNAPSHOT")
//addSbtPlugin("play" % "sbt-plugin" % "2.1-12142012")

//addSbtPlugin("org.ensime" % "ensime-sbt-cmd" % "0.0.10")
