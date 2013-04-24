package EnvStructs

import FileIO.WorkspaceManager
import java.nio.file.Paths
import FileIO.Readers.WorkspaceReader

object Context {
  var currentState = States.Main
  var curPres:Option[Project] = None
  var curWorkspace: Option[Workspace] = {
    val pathToWS = WorkspaceManager.locateDefaultWorkspace()
    pathToWS match {
      case None => {
        println("Workspace not found. Would you like to designate one now? yes:no")
        if (readLine() == "yes") {
          val path = Paths.get(Console.readLine("Enter path: "))
          WorkspaceManager.createNewWorkSpace(path)
        } else None
      }
      case Some(x) => {
        WorkspaceReader.read(x)
      }
    }
  }

  def tell() = {
    println("Current state: " + currentState.toString)
    println("Current project: " +
      {if(curPres.isDefined) curPres.get.title
      else "No project opened."})
    println("Current workspace: " + curWorkspace.get.wsName)
  }
}
