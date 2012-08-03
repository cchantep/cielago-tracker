import java.io.{ File, FileInputStream, FileOutputStream }

import java.sql.{ DriverManager, Connection, SQLException }

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

sealed trait DerbyTesting {
  private val testDbName = "target/testdb"

  private val testDbDir = new File(testDbName)

  private val testDbScripts =
    Seq("schema.sql", "fixtures.sql", "constr.sql")

  private def withConnection(block: Connection ⇒ Unit)(implicit logger: Logger): Unit = {
    val driverLoaded = try {
      Class.forName("org.apache.derby.jdbc.EmbeddedDriver").
        newInstance()

      true
    } catch {
      case t: Throwable ⇒ {
        logger.trace(t)
        false
      }
    }

    driverLoaded match {
      case false ⇒ logger.error("Fails to loader Derby driver")
      case true ⇒ {
        val conn = DriverManager.
          getConnection("jdbc:derby:" + testDbName + ";create=true")

        try {
          conn.setAutoCommit(false)

          block(conn)
        } catch {
          case t: Throwable ⇒ logger.trace(t)
        } finally {
          try {
            conn.close()
          } catch {
            case t: Throwable ⇒ logger.warn("Fails to close connection: %s" format t.getMessage)
          }

          try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true")
          } catch {
            case sql: SQLException ⇒ sql.getErrorCode match {
              case 50000 ⇒ logger.info("testdb successfully shutdown")
              case _     ⇒ logger.trace(sql)
            }
            case t: Throwable ⇒ logger.trace(t)
          }
        }
      }
    }
  }

  protected def testSetup()(implicit logger: Logger): Unit = {
    logger.info("Will set testdb up")

    def errorOnScript(script: String, errors: Int): Int = {
      logger.warn("Fails to run SQL script %s; errors = %s".
        format(script, errors))

      errors
    }

    def importScript(conn: Connection,
      script: FileInputStream,
      log: FileOutputStream): Int /*error count*/ =
      try { // @todo medium Loan pattern for stream
        DerbyTools.runScript(conn, script, "UTF-8", log, "UTF-8")
      } catch {
        case t: Throwable ⇒ {
          logger.trace(t)
          1
        }
      } finally {
        try {
          script.close()
        }
      }

    def withLogStream(block: FileOutputStream ⇒ Int): Int = {
      val log = new FileOutputStream("logs/testdb.log")

      try {
        block(log)
      } catch {
        case t: Throwable ⇒ {
          logger.trace(t)
          1
        }
      } finally {
        try {
          log.close()
        }
      }
    }

    withConnection { conn ⇒
      testDbScripts.foldLeft(0) { (n, script) ⇒
        withLogStream { log ⇒
          {
            logger.info("Will import SQL script: %s" format script)

            var res =
              importScript(conn,
                new FileInputStream("project/test/" + script),
                log)

            (n, res) match {
              case (a, -1)     ⇒ a
              case (b, 0)      ⇒ b
              case (c, errors) ⇒ c + errorOnScript(script, errors)
            }
          }
        }
      } match {
        case 0 /*No error running SQL scripts*/ ⇒ conn.commit()
        case ec ⇒ {
          logger.error("Fails to run SQL scripts; total errors = %s".
            format(ec))

        }
      }
    } // end of withConnection
  }

  protected def testCleanup(): Unit = FileUtils.deleteDirectory(testDbDir)

}

object ApplicationBuild extends Build
    with Resolvers with Dependencies with DerbyTesting {

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
