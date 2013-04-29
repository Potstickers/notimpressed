package EnvStructs

import FileIO.WorkspaceUtils
import java.nio.file.Paths
import FileIO.Readers.WorkspaceReader

/**
 * Context object. Tracks the current state of the program.
 * Main => User has not opened any projects.
 * Proj => User has opened a project.
 * Initialized at start of program. Heck, all objects are.
 * If you're a dev, and wondering what happens at all. Start here.
 */
object Context {
  var currentState = States.Main
  var curPres:Option[Project] = None
  var curWorkspace: Option[Workspace] = {
    val pathToWS = WorkspaceUtils.locateDefaultWorkspace()
    pathToWS match {
      case None => {
        println("Workspace not found. Would you like to designate one now? yes:no")
        if (readLine() == "yes") {
          val path = Paths.get(Console.readLine("Enter path: "))
          WorkspaceUtils.createNewWorkSpace(path)
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
