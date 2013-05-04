package EnvStructs

import collection.mutable.ArrayBuffer
import java.nio.file.{Files, Path}
import FileIO.{ProjectUtils, PathUtils}

/**
 * Defines a workspace.
 * @param homePath the path object of the workspace's directory.
 * @param projList The list of projects in this workspace.
 */
class Workspace(val homePath:Path, val projList:ArrayBuffer[String]) {

  val homeString = homePath.toString
  val wsName = homePath.getFileName.toString //used for printing prompt loop
  var changed = false
  /**
   * Adds a project to this workspace.
   * @param projPath The project to be added's path.
   */
  def addProject(projPath:String) {
    projList+=(projPath)
    changed = true
  }

  /**
   * Removes a project from this workspace.
   * @param projName name of the project.
   */
  def deleteProject(projName:String) {
    val projPath = PathUtils.toFullProjPath(projName, homeString)
    projList-=(projPath)
    ProjectUtils.deleteProject(projPath)
    changed = true
  }

  /**
   * Prints a listing of all the project(paths)
   * in this workspace.
   */
  def listProjects() {
    projList.foreach(println)
  }

  /**
   * Checks if a project exists in this directory.
   * @param name name of the project to be deleted.
   * @return true if workspace contains project, false otherwise.
   */
  def containsProject(name:String):Boolean = {
    if(projList.filter(_.endsWith(name)).size == 1)
      true
    else
      false
  }
}
