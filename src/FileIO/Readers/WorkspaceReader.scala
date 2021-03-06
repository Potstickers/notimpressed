package FileIO.Readers

import collection.mutable.ArrayBuffer
import EnvStructs._
import java.nio.file._
import java.nio.charset._
import collection.JavaConversions._
import FileIO.{PathUtils, WorkspaceUtils}

/**
 * Workspace reader. Handles reading of workspaces.
 */
object WorkspaceReader {
  /**
   * Reads a workspace by looking in directory provided by paths.
   * Reads from the notimpressed.ws file only instead of iterating
   * over the directory since this file provides the same information:
   * the absolute paths of the projects in this workspace.
   * @param path the path to the workspace to be read.
   * @return a workspace object on success, none otherwise.
   */
  def read(path:String):Option[Workspace] = {
    val normalizedPath = PathUtils.normalizePath(path)
    val wsDir:Path = Paths.get(normalizedPath) //file is a directory in this context
    try{
      val realPath = wsDir.toRealPath()
      val dirStream = Files.newDirectoryStream(realPath, "notimpressed.ws")
      val projList = Files.readAllLines(dirStream.iterator.next, StandardCharsets.UTF_8)
      dirStream.close()
      val convertedList = new ArrayBuffer[String](projList.size)
      convertedList ++= projList.toList
      Some(new Workspace(realPath, convertedList))
    }catch{
      case nde:NotDirectoryException => {
        println("The path entered is not a valid workspace.")
        None
      }
      case nsfe:NoSuchFileException => {
        println("No such directory. Create as new? yes : no")
        if (readLine() == "yes") {
          WorkspaceUtils.createNewWorkSpace(wsDir)
        } else None
      }
      case nsee:NoSuchElementException => {
        WorkspaceUtils.convertToWorkspace(wsDir)
      }
      case _:Throwable => {
        println("Something went wrong.")
        None
      }
    }
  }
}
