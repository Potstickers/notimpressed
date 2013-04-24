package FileIO.Readers

import EnvStructs.Project
import java.nio.file.{NotDirectoryException, Files, Paths}
import Entities.{CSSClass, Step}
import scala.collection.mutable._
import java.io.IOException
import FileIO.{PathUtils, ProjectManager}
import Parser.ProjectParser

object ProjectReader {
  def read(path: String): Option[Project] = {
    val projPath = Paths.get(PathUtils.normalizePath(path))
    try {
      val dirStream = Files.newDirectoryStream(projPath.toRealPath(), "notimpressed.*")
      val itor = dirStream.iterator()
      //todo: refactor to use case classes + recursion
      var pres:(String,ArrayBuffer[Step]) = null
      var css:ArrayBuffer[CSSClass] = null
      /* todo: Robustness - create new files for missing files. */
      while (itor.hasNext) {
        val path = itor.next()
        if (path.endsWith("notimpressed.pres"))
          pres = ProjectParser.parsePres(path)
        else
          css = ProjectParser.parseCSSRules(path)
      }
      dirStream.close()
      Some(new Project(projPath,pres._1,pres._2,css))
    }catch{
      case nde:NotDirectoryException => {
        println("Not a valid project directory.")
        None
      }
      case ioe:IOException => {
        val answer = readLine("Project not found. Would you like to create as new? yes:no")
        if (answer == "yes")
          ProjectManager.createNewProject(
            projPath.getFileName.toString,
            projPath.toString)
        else None
      }
      case _:Throwable => {
        println("Something went wrong.")
        None
      }
    }
  }
}
