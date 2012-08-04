import java.io.File

import sbt._
import Keys._
import PlayProject._

import atewaza.DerbyTesting

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
    with Resolvers with Dependencies with DerbyTesting {

  override val atewazaDbPath = "target/testdb"
  override val atewazaDbScripts =
    Seq("schema", "constr", "fixtures") map { n ⇒
      new File("project/test/" + n + ".sql")
    }

  lazy val main = PlayProject(
    "Cielago-tracker",
    "1.0-SNAPSHOT",
    mainLang = SCALA).settings(
      resolvers ++= Seq(sonatype),
      libraryDependencies ++= compile
        ++ test.map { dep ⇒ dep % "test" }
        ++ atewazaTestDependencies
        ++ runtime.map { dep ⇒ dep % "runtime" },
      testOptions := Seq(Tests.Setup(atewazaSetup _),
        Tests.Cleanup(atewazaCleanup _)),
      scalacOptions := Seq("-deprecation", "-unchecked"))

}
