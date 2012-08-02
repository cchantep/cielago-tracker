import java.io.{ File, FileInputStream, FileOutputStream }

import java.sql.{ DriverManager, Connection }

import org.apache.commons.io.FileUtils

import org.apache.derby.tools.{ ij ⇒ DerbyTools }

import sbt._
import Keys._
import PlayProject._

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
    Seq( /*Setup,Cleanup*/
      "commons-io" % "commons-io" % "2.4",
      "org.apache.derby" % "derby" % "10.9.1.0",
      /*Specs*/
      "commons-codec" % "commons-codec" % "1.6")

  val runtime =
    Seq("postgresql" % "postgresql" % "9.1-901.jdbc4")

}

sealed trait Testing {
  private val testDbName = "project/testdb"

  private val testDbDir = new File(testDbName)

  private val testDbScripts =
    Seq("schema.sql", "fixtures.sql", "constr.sql")

  private def testConnection()(implicit logger: Logger): Option[Connection] =
    try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").
        newInstance()

      val conn = DriverManager.
        getConnection("jdbc:derby:" + testDbName + ";create=true")

      conn.setAutoCommit(false)

      Some(conn)
    } catch {
      case t: Throwable ⇒ {
        logger.trace(t)
        None
      }
    }

  protected def testSetup()(implicit logger: Logger): Unit = {
    def errorOnScript(script: String, errors: Int): Int = {
      logger.warn("Fails to run SQL script %s; errors = %s".
        format(script, errors))

      -1
    }

    testConnection match {
      case None ⇒ logger.error("No test connection available")

      case Some(conn) ⇒ {
        testDbScripts.foldLeft(0) { (n, script) ⇒
          try { // @todo medium Loan pattern for stream
            val scriptFile = new FileInputStream("project/test/" + script)
            val res =
              DerbyTools.runScript(conn,
                scriptFile,
                "UTF-8",
                new FileOutputStream("logs/testdb.log"),
                "UTF-8")

            (n, res) match {
              case (a, -1)     ⇒ a
              case (b, 0)      ⇒ b
              case (c, errors) ⇒ c + errorOnScript(script, errors)
            }
          } catch {
            case t: Throwable ⇒ {
              logger.trace(t)
              -1
            }
          }
        } match {
          case 0 /*No error running SQL scripts*/ ⇒ conn.commit()
          case ec ⇒ {
            logger.error("Fails to run SQL scripts; total errors = %s".
              format(ec))

          }
        }
      }
    } // end of testConnection match
  }

  protected def testCleanup(): Unit = {
    FileUtils.deleteDirectory(testDbDir)
  }
}

object ApplicationBuild extends Build
    with Resolvers with Dependencies with Testing {

  implicit val logger = ConsoleLogger()

  lazy val main = PlayProject(
    "Cielago-tracker",
    "1.0-SNAPSHOT",
    mainLang = SCALA).settings(
      resolvers ++= Seq(sonatype),
      libraryDependencies ++= compile
        ++ test.map { dep ⇒ dep % "test" }
        ++ runtime.map { dep ⇒ dep % "runtime" },
      testOptions := Seq(Tests.Setup(testSetup _),
        Tests.Cleanup(testCleanup _)),
      scalacOptions := Seq("-deprecation", "-unchecked"))

}
