package Command

import collection.mutable.HashMap
import FileIO.{PathUtils, ProjectManager, WorkspaceManager}
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

  def execute(cmdArgs: (String, HashMap[String, String])) = {
    cmdArgs._1 match {
      case "new" => handleNew(cmdArgs._2)
      case "open" => handleOpen(cmdArgs._2)
      case "list" => handleList(cmdArgs._2)
      case "exit" => handleExit()
      case "tell" => Context.tell()
      case _ => println("Unrecognized command.")
    }
  }

  private def createNew(name: String, wsPath: Path) {
    val projPath = PathUtils.toFullProjPath(name, wsPath.toString)
    Context.curWorkspace.get.addProject(projPath)
    Context.curPres = ProjectManager.createNewProject(name,projPath)
    Context.currentState = States.Proj
  }

  private def openExisting(name: String, wsPath: Path) {
    val projPath = PathUtils.toFullProjPath(name, wsPath.toString)
    Context.curPres = ProjectReader.read(projPath)
    Context.currentState = States.Proj
  }
  private def handleList(args: HashMap[String,String]) = {
    println("Projects in this workspace:")
    if(args.contains("in"))
      Files.readAllLines(Paths.get(args("in")),
        StandardCharsets.UTF_8).
        toList.foreach(println)
    else Context.curWorkspace.get.listProjects()
  }
  private def handleNew(args: HashMap[String, String]) = {
    if (args.contains("name")) {
      val presName = args("name")
      val wsPathStr = args.getOrElse("in", resolveWSPath())
      if(Context.curWorkspace.get.homeString != wsPathStr)
        Context.curWorkspace = WorkspaceReader.read(wsPathStr)
      if(Context.curWorkspace.get.containsProject(presName)){
        val answer = readLine("Project already exists. Open anyway? yes:no")
        if(answer == "yes")
          openExisting(presName, Context.curWorkspace.get.homePath)
      }else
        createNew(presName,Context.curWorkspace.get.homePath)
    } else println("Presentation name not specified.")
  }

  private def handleOpen(args: HashMap[String, String]) = {
    if (args.contains("name")) {
      val presName = args("name")
      val wsPathStr = args.getOrElse("in", resolveWSPath())
      if(Context.curWorkspace.get.homeString != wsPathStr)
        Context.curWorkspace = WorkspaceReader.read(wsPathStr)
      if(!Context.curWorkspace.get.containsProject(presName)){
        println("Project does not exist. Create as new? yes:no")
        if(readLine() == "yes")
          createNew(presName, Context.curWorkspace.get.homePath)
      }else
        openExisting(presName, Context.curWorkspace.get.homePath)
    } else println("Presentation name not specified.")
  }

  private def resolveWSPath():String = {
    if(Context.curWorkspace.isDefined)
      Context.curWorkspace.get.homeString
    else{
      val defaultWS = WorkspaceManager.locateDefaultWorkspace()
      if(defaultWS.isDefined)
        defaultWS.get
      else{
        val path = readLine("No workspace set. Enter path to workspace now: ")
        Context.curWorkspace = WorkspaceReader.read(path)
        path
      }
    }
  }
  private def handleExit() {
    println("Byeeeeeeee!")
    if(Context.curWorkspace.isDefined)
      WorkspaceWriter.write(Context.curWorkspace.get)
    System.exit(0)
  }
}
