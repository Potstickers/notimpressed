package FileIO

import java.nio.file._
import EnvStructs.Workspace
import scala.collection.mutable.ArrayBuffer
import java.io.IOException
import java.nio.charset.StandardCharsets
import scala.Some
import FileIO.Readers.WorkspaceReader

/**
 * Utils for some common routines dealing with workspaces.
 */
object WorkspaceUtils {
  val userConfigDir = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed"
  val userConfigFile = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed/.conf"
  val impressFile = System.getProperty("user.home") +
    PathUtils.separator +".notimpressed/impress-mini.js"

  /**
   * Creates a new workspace given a path to
   * directory to be designated as a workspace by
   * creating that directory if d.n.e and converting it
   * to a workspace. see convertToWorkspace.
   * @param dirPath the path to the directory.
   * @return a new workspace object on success, none otherwise.
   */
  def createNewWorkSpace(dirPath: Path): Option[Workspace] = {
    try{
    val newPath: Path = Files.createDirectory(dirPath)
    saveAsDefault(newPath)
    convertToWorkspace(newPath)
    }catch{
      case fae:FileAlreadyExistsException =>
        saveAsDefault(dirPath)
        WorkspaceReader.read(dirPath.toString)
    }
  }

  /**
   * Converts given path to a workspace by writing
   * a notimpressed.ws file inside that directory.
   * @param dirPath the workspace path to be converted
   * @return a new workspace object on success, none otherwise
   */
  def convertToWorkspace(dirPath: Path): Option[Workspace] = {

    try {
      Files.createFile(Paths.get(PathUtils.
        normalizePath(dirPath.toString + "/notimpressed.ws")))
      /**
       * todo: scan directory for subdirectories that contain
       * .pres files and include in construction.
       */
      Some (new Workspace(dirPath, new ArrayBuffer[String](1)))
    } catch {
      case _: Throwable =>
        println("Unable to save directory.")
        None
    }
  }

  /**
   * Locates the default workspace by reading
   * the notimpressed.conf file found in the .notimpressed
   * directory located in the user directory. The first line
   * in this file, is the path to the workspace directory.
   * @return The path found in notimpressed.conf.
   */
  def locateDefaultWorkspace(): Option[String] = {
    try {
      Paths.get(userConfigDir).toRealPath()
      val configFilePath = Paths.get(userConfigFile).toRealPath()
      val defaultWS = Files.readAllLines(configFilePath,
        StandardCharsets.UTF_8)
      if (defaultWS.size() > 0) Some(defaultWS.get(0)) else None
    } catch {
      case ioe: IOException => {
        //should really only happen on first runs
        //todo: detect if just missing file or dir
        ioe.getMessage.endsWith("impressed") match {
          case true => Files.createDirectory(Paths.get(userConfigDir))
            println("Make sure to grab a copy of impress-mini.js " +
              "from\nhttps://github.com/Potstickers/notimpressed/" +
              "tree/master/js/impress-mini.js " +
              "and stick it in\n" + userConfigDir)
            Files.createFile(Paths.get(userConfigFile))
          case false => Files.createFile(Paths.get(userConfigFile))
        }
        None
      }
    }
  }

  /**
   * Saves a workspace path as the default workspace by
   * writing the the path string to the .conf file in .notimpressed
   * directory.
   * @param path the workspace path to be saved.
   */
  def saveAsDefault(path: Path) {
    try {
      //todo: something feels off with this...
      val realPath:Path = Paths.get(userConfigFile).toRealPath()
      Files.write(realPath, path.toString.getBytes, StandardOpenOption.WRITE)
    } catch {
      case e: Exception => println("Unable to save as default workspace.")
    }
  }

}
