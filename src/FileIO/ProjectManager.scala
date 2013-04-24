package FileIO

import java.nio.file.{StandardOpenOption, Paths, Files}
import EnvStructs.Project
import Entities.{CSSClass, Step}
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.nio.charset.StandardCharsets

object ProjectManager {

  def createNewProject(name:String, wsPathString:String):Option[Project] = {
    val path = Paths.get(wsPathString)
    Files.createDirectory(path)
    convertToProject(name, wsPathString)
  }

  def convertToProject(name:String, pathString:String):Option[Project] = {
    val path = Paths.get(pathString)
    val cssFile = Paths.get(PathUtils.normalizePath(pathString + "/notimpressed.css"))
    val presFile = Paths.get(PathUtils.normalizePath(pathString + "/notimpressed.pres"))
    try{
      Files.createFile(cssFile)
      val presPath = Files.createFile(presFile)
      val titleAsBytes = ("title:"+name+'\n').getBytes(StandardCharsets.UTF_8)
      Files.write(presPath,titleAsBytes, StandardOpenOption.WRITE)
      //todo: copy over impress.js from user directory here
      Some(new Project(path, name,
        new ArrayBuffer[Step](),
        new ArrayBuffer[CSSClass]))
    }catch{
      case ioe:IOException => {
        println("Unable to convert to project directory.")
        None
      }
      case _:Throwable => {
        println("Something went wrong.")
        None
      }
    }
  }
}
