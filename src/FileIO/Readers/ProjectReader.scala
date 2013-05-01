package FileIO.Readers

import EnvStructs.Project
import java.nio.file.{NotDirectoryException, Files, Paths}
import Entities.{CSSClass, Step}
import scala.collection.mutable._
import java.io.IOException
import FileIO.{PathUtils, ProjectUtils}
import Parser.ProjectParser

/**
 * Project reader. Handles reading of project.
 */
object ProjectReader {
  /**
   * Reads a project by looking in directory provided by path.
   * Handles cases of invalid project directories and nonexisting
   * directories.
   * @param path The path of the project directory.
   * @return the read Project on success, None otherwise.
   */
  def read(path: String): Option[Project] = {
    val normPath = PathUtils.normalizePath(path)
    val projPath = Paths.get(normPath)
    try {
      val dirStream = Files.newDirectoryStream(projPath.toRealPath(),
        "notimpressed.*")
      val itor = dirStream.iterator()
      //todo: refactor to use case classes + recursion
      var pres:(String,ArrayBuffer[Step]) = null
      var css:ArrayBuffer[CSSClass] = null
      /* todo: Robustness - create new files for missing files. */
      while (itor.hasNext) {
        val path = itor.next()
        path.getFileName.toString
          .endsWith("pres") match {
          case true => pres = ProjectParser.parsePres(path)
          case false => css = ProjectParser.parseCSSRules(path)
        }
      }
      dirStream.close()
      Some(new Project(normPath,pres._1,pres._2,css))
    }catch{
      case nde:NotDirectoryException => {
        println("Not a valid project directory.")
        None
      }
      case ioe:IOException => {
        val answer = readLine("Project not found. Would you like to create as new? yes:no")
        if (answer == "yes")
          ProjectUtils.createNewProject(
            projPath.getFileName.toString,
            normPath)
        else None
      }
      case _:Throwable => {
        println("Something went wrong.")
        None
      }
    }
  }
}
