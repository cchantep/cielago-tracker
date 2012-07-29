import sbt._
import Keys._
import PlayProject._

trait Resolvers {
  val iliaz = "iliaz.com" at "http://scala.iliaz.com/"
}

trait Dependencies {
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"

  val postgresql = "postgresql" % "postgresql" % "9.1-901.jdbc4"
  val derby = "org.apache.derby" % "derby" % "10.9.1.0"
}

object ApplicationBuild extends Build with Resolvers with Dependencies {

  lazy val main = PlayProject(
    "Cielago-tracker",
    "1.0-SNAPSHOT",
    mainLang = SCALA).settings(
      resolvers ++= Seq(iliaz),
      libraryDependencies ++= Seq(scalaz, postgresql),
      libraryDependencies in Test := Seq(derby),
      scalacOptions := Seq("-deprecation", "-unchecked"))
}
