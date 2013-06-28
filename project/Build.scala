import java.io.File

import sbt._
import Keys._
import play.Project._

sealed trait Resolvers {
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
  val applicius = "Applicius Snapshots" at "https://raw.github.com/applicius/mvn-repo/master/snapshots/"

}

/**
 * Dependency definitions
 */
sealed trait Dependencies {
  val compile =
    Seq("org.scalaz" %% "scalaz-core" % "7.0.0", jdbc, anorm)

  val test =
    Seq(
      "org.specs2" %% "specs2" % "1.14",
      "commons-codec" % "commons-codec" % "1.7",
      "acolyte" %% "acolyte-scala" % "1.0.2")

  val runtime =
    Seq("postgresql" % "postgresql" % "9.1-901.jdbc4")

}

object ApplicationBuild extends Build
    with Resolvers with Dependencies {

  lazy val appDependencies = 
    compile ++ test.map { dep ⇒ dep % "test" } ++ runtime.
      map { dep ⇒ dep % "runtime" }

  lazy val main = play.Project("Cielago-tracker", "1.0.2", appDependencies).
    settings(
      scalaVersion := "2.10.2",
      scalacOptions := Seq("-deprecation", "-unchecked", "-feature"),
      resolvers := Seq(sonatype, applicius))

}
