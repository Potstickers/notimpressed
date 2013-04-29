package FileIO

import java.nio.file.{StandardOpenOption, Paths, Files, Path}
import EnvStructs.Workspace
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.nio.charset.StandardCharsets

object WorkspaceUtils {
  val userConfigDir = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed"
  val userConfigFile = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed/.conf"
  val impressFile = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed/impress-mini.js"

  def createNewWorkSpace(dirPath: Path): Option[Workspace] = {
    val newPath: Path = Files.createDirectory(dirPath)
    saveAsDefault(newPath)
    convertToWorkspace(newPath)
  }

  def convertToWorkspace(dirPath: Path): Option[Workspace] = {
    try {
      val normedPath: String = PathUtils.normalizePath(dirPath.toString + "/notimpressed.ws")
      Files.createFile(Paths.get(normedPath))
      Some (new Workspace(dirPath, new ArrayBuffer[String](1)))
    } catch {
      case e: Exception => println("Unable to save directory."); None
    }
  }

  def locateDefaultWorkspace(): Option[String] = {
    try {
      val configFilePath = Paths.get(userConfigFile).toRealPath()
      val defaultWS = Files.readAllLines(configFilePath, StandardCharsets.UTF_8)
      if (defaultWS.size() > 0) Some(defaultWS.get(0)) else None
    } catch {
      case ioe: IOException => {
        //should really only happen on first runs
        //todo: detect if just missing file or dir
        Files.createDirectory(Paths.get(userConfigDir))
        Files.createFile(Paths.get(userConfigFile))
        None
      }
    }
  }

  def saveAsDefault(path: Path) = {
    try {
      val realPath:Path = Paths.get(userConfigFile).toRealPath()
      Files.write(realPath, path.toString.getBytes, StandardOpenOption.WRITE)
    } catch {
      case e: Exception => println("Unable to save as default workspace.")
    }
  }

}
