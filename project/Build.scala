import java.io.File

import sbt._
import Keys._
//import PlayProject._
import play.{Project=>PlayProject}

sealed trait Resolvers {
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
}

/**
 * Dependency definitions
 */
sealed trait Dependencies {
  val compile =
    Seq("org.scalaz" %% "scalaz-core" % "6.0.4")

  val test =
    Seq( /*Specs*/
      "commons-codec" % "commons-codec" % "1.6")

  val runtime =
    Seq("postgresql" % "postgresql" % "9.1-901.jdbc4")

}

object ApplicationBuild extends Build
    with Resolvers with Dependencies {

  lazy val main = PlayProject(
    "Cielago-tracker",
    "1.0.0").settings(
      resolvers ++= Seq(sonatype),
      libraryDependencies ++= compile
        ++ test.map { dep ⇒ dep % "test" }
        ++ runtime.map { dep ⇒ dep % "runtime" },
      scalaVersion := "2.9.2",
      scalacOptions := Seq("-deprecation", "-unchecked"))

}
