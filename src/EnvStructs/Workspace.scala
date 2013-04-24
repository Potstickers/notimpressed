package EnvStructs

import collection.mutable.ArrayBuffer
import java.nio.file.Path
import FileIO.PathUtils

class Workspace(val homePath:Path, val projList:ArrayBuffer[String]) {

  val homeString = homePath.toString
  val wsName = homePath.getFileName.toString

  def addProject(projPath:String) = {
    projList+=(projPath)
  }
  def deleteProject(name:String) = {
    //When switching workspace or program exit, scan for differences and delete actuals
    projList-=(PathUtils.toFullProjPath(name, homeString))
  }
  def listProjects() {
    projList.foreach(println)
  }
  def containsProject(name:String):Boolean = {
    if(projList.filter(_.endsWith(name)).size == 1)
      true
    else
      false
  }
}
