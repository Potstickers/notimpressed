package EnvStructs

import FileIO.WorkspaceUtils
import java.nio.file.Paths
import FileIO.Readers.WorkspaceReader

/**
 * Context object. Tracks the current state of the program.
 * Main => User has not opened any projects.
 * Proj => User has opened a project.
 * In addition to the current workspace and project/presentation
 */
object Context {
  var state = States.Main
  var pres:Option[Project] = None
  var workspace: Option[Workspace] = {
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

  /**
   * Prints information about this Context.
   */
  def tell() = {
    println("Current state: " + state.toString)
    println("Current project: " +
      {if(pres.isDefined) pres.get.title
      else "No project opened."})
    println("Current workspace: " + workspace.get.wsName)
  }
}
