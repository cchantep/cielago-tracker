import sbt._
import Keys._
import PlayProject._

trait Resolvers {
  val sonatype = "sonatype" at "http://oss.sonatype.org/content/repositories/releases"
}

trait Dependencies {
  val scalaz = "org.scalaz" %% "scalaz-core" % "6.0.4"

  val postgresql = "postgresql" % "postgresql" % "9.1-901.jdbc4"

  val derby = "org.apache.derby" % "derby" % "10.9.1.0"

  val codec = "commons-codec" % "commons-codec" % "1.6"
}

object ApplicationBuild extends Build with Resolvers with Dependencies {

  lazy val main = PlayProject(
    "Cielago-tracker",
    "1.0-SNAPSHOT",
    mainLang = SCALA).settings(
      resolvers ++= Seq(sonatype),
      libraryDependencies ++= 
        Seq(scalaz, 
            postgresql, 
            derby % "test",
            codec % "test"),
      scalacOptions := Seq("-deprecation", "-unchecked"))
}
