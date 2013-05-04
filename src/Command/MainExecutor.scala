package Command

import collection.mutable.HashMap
import FileIO.{PathUtils, ProjectUtils, WorkspaceUtils}
import FileIO.Readers.{ProjectReader, WorkspaceReader}
import EnvStructs.{States, Context}
import java.nio.file.{Paths, Files, Path}
import collection.JavaConversions._
import java.nio.charset.StandardCharsets
import FileIO.Writers.WorkspaceWriter

/**
 * Command executor when in Main state.
 */
object MainExecutor {
  /**
   * Executes acceptable commands in Main state
   * Acceptable commands include: {new, open, list, exit, tell}
   * @param cmdArgs parsed tuple of(cmd, args) to be executed.
   */
  def execute(cmdArgs: (String, HashMap[String, String])) = {
    cmdArgs._1 match {
      case "new" => handleNew(cmdArgs._2)
      case "open" => handleOpen(cmdArgs._2)
      case "list" => handleList(cmdArgs._2)
      case "delete" => handleDelete(cmdArgs._2)
      case "exit" => handleExit()
      case "tell" => Context.tell()
      case _ => println("Unrecognized command.")
    }
  }

  /**
   * Minimal routine to create a new project.
   * Changes state to Proj on success.
   * @param name The name of the project.
   * @param wsPath The path of the workspace directory
   *               to put the project in.
   */
  private def createNew(name: String, wsPath: Path) {
    val projPath = PathUtils.toFullProjPath(name, wsPath.toString)
    Context.workspace.get.addProject(projPath)
    Context.pres = ProjectUtils.createNewProject(name, projPath)
    Context.state = States.Proj
  }

  /**
   * Minimal routine to open a project.
   * Changes state to Proj on success.
   * @param name The name of the project to be opened.
   * @param wsPath The path of the workspace directory
   *               to read from.
   */
  private def openExisting(name: String, wsPath: Path) {
    val projPath = PathUtils.toFullProjPath(name, wsPath.toString)
    Context.pres = ProjectReader.read(projPath)
    Context.state = States.Proj
  }

  /**
   * Routine to handle listing of projects.
   * @param args the arguments. Should only contain "in"
   *             if listing in workspace other the current.
   */
  private def handleList(args: HashMap[String, String]) = {
    println("Projects in this workspace:")
    if (args.contains("in"))
      Files.readAllLines(Paths.get(args("in")),
        StandardCharsets.UTF_8).
        toList.foreach(println)
    else Context.workspace.get.listProjects()
  }

  /**
   * Routine to handle new command for creating new projects.
   * @param args the arguments. Should only contain
   *             {"name" = project name, "in" = workspace}
   */
  private def handleNew(args: HashMap[String, String]) = {
    if (args.contains("name")) {
      val presName = args("name")
      val wsPathStr = args.getOrElse("in", resolveWSPath())
      updateWorkspace(wsPathStr)
      if (Context.workspace.get.containsProject(presName)) {
        val answer = readLine("Project already exists." +
          " Open anyway? yes:no")
        if (answer == "yes")
          openExisting(presName, Context.workspace.get.homePath)
      } else
        createNew(presName, Context.workspace.get.homePath)
    } else println("Presentation name not specified.")
  }

  /**
   * Routine to handle open command for opening existing projects.
   * @param args the arguments. Should only contain
   *             {"name" = project name, "in" = workspace}
   */
  private def handleOpen(args: HashMap[String, String]) = {
    if (args.contains("name")) {
      val presName = args("name")
      val wsPathStr = args.getOrElse("in", resolveWSPath())
      updateWorkspace(wsPathStr)
      if (!Context.workspace.get.containsProject(presName)) {
        println("Project does not exist. Create as new? yes:no")
        if (readLine() == "yes")
          createNew(presName, Context.workspace.get.homePath)
      } else
        openExisting(presName, Context.workspace.get.homePath)
    } else println("Presentation name not specified.")
  }

  /**
   * Routine to get the current workspace path string if the "in" argument
   * was not provided by user. If workspace is already defined in the
   * Context object, returns that string. If no workspace set, tries to go
   * out to notimpressed.conf to locate the default workspace. If not found
   * there, prompts user to enter. Which user should really do, unless user
   * has high tolerance of annoyance.
   * @return The path string of the current workspace.
   */
  private def resolveWSPath(): String = {
    if (Context.workspace.isDefined)
      Context.workspace.get.homeString
    else {
      val defaultWS = WorkspaceUtils.locateDefaultWorkspace()
      if (defaultWS.isDefined)
        defaultWS.get
      else {
        val path = readLine("No workspace set. " +
          "Enter path to workspace now: ")
        Context.workspace = WorkspaceReader.read(path)
        path
      }
    }
  }

  /**
   * Routine to handle cases when workspace
   * has changed or not specified.
   * @param wsPath user entered path to workspace.
   */
  private def updateWorkspace(wsPath: String) {
    if (Context.workspace.isDefined) {
      if (Context.workspace.get.homeString != wsPath) {
        Context.workspace = WorkspaceReader.read(wsPath)
        WorkspaceUtils.saveAsDefault(Paths.get(wsPath))
      } //else no change, working in same workspace
    } else {
      Context.workspace = WorkspaceReader.read(wsPath)
      WorkspaceUtils.saveAsDefault(Paths.get(wsPath))
    }
  }

  /**
   * Saves the current workspace and terminates normally.
   */
  private def handleExit() {
    println("Byeeeeeeee!")
    if (Context.workspace.isDefined)
      WorkspaceWriter.write(Context.workspace.get)
    System.exit(0)
  }

  /**
   * Routine to handle deleting of projects.
   * Does not change the current workspace if "in"
   * is defined. If a workspace is not defined either
   * in current workspace or specified by "in", does nothing.
   * @param args the arguments. should only contain {name,in}
   */
  private def handleDelete(args: HashMap[String, String]) {
    if (args.contains("name")) {
      val workspace = {
        if (Context.workspace.isEmpty && args.contains("in"))
          WorkspaceReader.read(args("in"))
        else if (Context.workspace.isDefined)
          Context.workspace
        else None
      }
      if (workspace.isDefined &&
        workspace.get.containsProject(args("name"))) {
        ProjectUtils.deleteProject(PathUtils.
          toFullProjPath(args("name"), workspace.get.homeString))
        workspace.get.deleteProject(args("name"))
      } else {
        println("No workspace opened or " +
          "workspace does not contain project.")
      }
    } else {
      println("No name specified. Please specify a name.")
    }
  }
}
