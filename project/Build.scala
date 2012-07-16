import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  lazy val main = PlayProject("Cielago-tracker", "1.0-SNAPSHOT", mainLang = SCALA).settings(
    resolvers ++= Seq(
      "iliaz.com" at "http://scala.iliaz.com/",
      "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
    ),
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % "6.0.4",
      "postgresql" % "postgresql" % "9.1-901.jdbc4",
      "jp.t2v" %% "play20.auth" % "0.2"
    ),
    scalacOptions := Seq("-deprecation", "-unchecked")
  )
}
