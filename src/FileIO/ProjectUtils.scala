package FileIO

import java.nio.file._
import EnvStructs.Project
import Entities.{CSSClass, Step}
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.nio.charset.StandardCharsets
import scala.Some
import java.nio.file.attribute.BasicFileAttributes

/**
 * Utils for some common routines dealing with projects.
 */
object ProjectUtils {
  /**
   * Creates a new project by creating a directory specified by
   * given project path and then converts to project. see
   * convertToProject.
   * @param name name of the project
   * @param projPathString the path where
   *                       this project is to be located
   * @return The new project on success, none otherwise.
   */
  def createNewProject(name: String,
                       projPathString: String): Option[Project] = {
    val path = Paths.get(projPathString)
    Files.createDirectory(path)
    convertToProject(name, projPathString)
  }

  /**
   * Converts a given path to a valid project directory by
   * writing the following files to the directory:
   * notimpressed.css
   * notimpressed.pres
   * and copying over impress-mini.js
   * @param name the name of this presentation
   * @param pathString the directory where project is located.
   * @return A new project object on success, none otherwise.
   */
  def convertToProject(name: String,
                       pathString: String): Option[Project] = {
    val cssFile = Paths.get(PathUtils.normalizePath(pathString
      + "/notimpressed.css"))
    val presFile = Paths.get(PathUtils.normalizePath(pathString
      + "/notimpressed.pres"))
    try {
      Files.createFile(cssFile)
      val presPath = Files.createFile(presFile)
      val titleAsBytes = ("title:" + name + '\n')
        .getBytes(StandardCharsets.UTF_8)
      Files.write(presPath, titleAsBytes, StandardOpenOption.WRITE)
      ProjectUtils.copyOverImpress(pathString)
      Some(new Project(pathString, name,
        new ArrayBuffer[Step](),
        new ArrayBuffer[CSSClass]))
    } catch {
      case ioe: IOException => {
        println("Unable to convert to project directory.")
        None
      }
      case _: Throwable => {
        println("Something went wrong.")
        None
      }
    }
  }

  /**
   * Copies impress-mini.js from user directory
   * to given project directory.
   * @param projPath path to project directory
   */
  def copyOverImpress(projPath: String) {
    //todo: exception vs checking directory
    try {
      val dirStream = Files.newDirectoryStream(
        Paths.get(projPath), "impress-mini.js")
      if (!dirStream.iterator().hasNext){
        val destPath = PathUtils.
          normalizePath(projPath+"/impress-mini.js")
        Files.copy(Paths.get(WorkspaceUtils.impressFile),
          Paths.get(destPath))
      }
      dirStream.close()
    } catch {
      case ioe: IOException => println(ioe.getMessage)
      case _: Throwable => println("You broke it!")
    }
  }

  /**
   * Deletes a project. Deletes project directory completely
   * if the directory contained only program generated files.
   * Will leave the directory and unrelated sub files/directories
   * alone if encountered.
   * @param projPath The path to the project to be deleted.
   */
  def deleteProject(projPath: String) {
    scala.util.control.Exception.ignoring(_:Class[Exception]){
      val path = Paths.get(PathUtils.normalizePath(projPath))
      Files.walkFileTree(path, new SimpleFileVisitor[Path] {
        override def visitFile
        (file: Path, attrs: BasicFileAttributes): FileVisitResult = {
          file.getFileName.toString match{
            case "notimpressed.pres" |
                 "notimpressed.css" |
                 "impress-mini.js" |
                 "presentation.html" => Files.delete(file)
          }
          FileVisitResult.CONTINUE
        }

        override def postVisitDirectory
        (dir: Path, ioe: IOException): FileVisitResult = {
          ioe match {
            case null =>
              Files.delete(dir)
              FileVisitResult.CONTINUE
            case _ => throw ioe
          }
        }
      })
      Files.delete(path)
    }
  }
}
