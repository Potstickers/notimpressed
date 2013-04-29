package Command

import EnvStructs.{States, Context}
import FileIO.Writers.ProjectWriter
import Entities.CSSClass
import collection.mutable._
import FileIO.ProjectUtils

/**
 * Command executor when in Proj state.
 */
object ProjExecutor {

  def execute(cmdArgs:(String,HashMap[String,String])) = {
    cmdArgs._1 match {
      case "commit" => handleCommit()
      case "close" => handleClose()
      case "new" => handleNew(cmdArgs._2)
      case "tell" => handleTell(cmdArgs._2)
      case cmd if (cmd.startsWith("css") ||
        cmd.startsWith("step")) => handleUpdate(cmd, cmdArgs._2)
      case _ => println("Command not recognized in this context.")
    }
  }

  /**
   * Adds a new item (css class or step)
   * @param args The attributes of the item
   */
  private def handleNew(args:HashMap[String,String]) = {
    if(args.contains("item")){
      args("item") match{
        case "step" =>
          if(args.contains("id"))
            if(Context.curPres.get.getStep(args("id")).isDefined)
              println("A step by that id already exists.")
            else
              Context.curPres.get.addStep(args-("item"))
          else
            Context.curPres.get.addStep(args-("item"))
        case "css" =>
          if (args.contains("name") && args("name").length > 0)
            Context.curPres.get.cssRules += (
              new CSSClass(args("name"),
                args-("name", "item")))
          else
            println("CSS class name not specified.")
      }
    }else
      println("No item specified." +
        "Specify an item to be either step or css.")
  }

  /**
   * Updates the set of attributes of an existing Step or CSSClass.
   * @param item The string indicating a step or css class. Should
   *             follow the convention:
   *             "css(<className>)" or "step(<# or id>)"
   * @param args The set of attributes to be updated.
   */
  private def handleUpdate(item:String, args:HashMap[String,String]) = {
    val key = item.substring(item.indexOf('(')+1,
              item.indexOf(')')).trim
    if(item.startsWith("css")){
      val css = Context.curPres.get.getCSSClass(key)
      if(css.isDefined)
        args.foreach(css.get.set)
      else{
        readLine("Would you like to create as new? yes:no ").trim
        match {
          case "yes" =>
            //todo: handle key not strictly alphabetic
            args+=(("item","css"),("name", key))
            handleNew(args)
          case _ => //No action
        }
      }
    } else {
      val step = Context.curPres.get.getStep(key)
      if(step.isDefined)
        args.foreach(step.get.set)
      else{
        readLine("No step by found for key: \""+ key +"\". " +
          "Would you like to create as new? yes:no ").trim
        match {
          case "yes" =>
            args+=(("item","step"))
            //todo: make more robust than this
            if(key.charAt(0).isLetter)
              args+=(("id", key))
            handleNew(args)
          case _ => //No action
        }
      }
    }
  }

  /**
   * Generates the html of this project and copies over the
   * impress-mini.js file if needed.
   */
  private def handleCommit() {
    ProjectWriter.writePresHTML(Context.curPres.get)
    ProjectUtils.copyOverImpress(Context.curPres.get.homePath)
  }

  /**
   * Saves all changes and returns to main state.
   */
  private def handleClose() {
    ProjectWriter.write(Context.curPres.get)
    Context.currentState = States.Main
    Context.curPres = None
  }

  /**
   * Prints details about a optional given item.
   * If none is specified, prints everything about the project.
   * todo: re-impl to follow convention below.
   * @param item Optional item (css or step).
   *             Should be or form:
   *             "css(<className>)" or "step(<# or id>)"
   */
  private def handleTell(args:HashMap[String,String]) {
    if(args.size == 0){
      listEverything()
    }else{
      if(args.size == 1){
        if(args.contains("step")){
          val step = Context.curPres.get
            .getStep(args("step"))
          step match {
            case Some(s) => s.tell()
            case None => println("No step by that accessor found.")
          }
        }else if(args.contains("css")){
          val cssClass = Context.curPres.get
            .getCSSClass(args("css"))
          cssClass match {
            case Some(c) => c.tell()
            case None => println("No class by that accessor found.")
          }
        }
        else
          println("Wrong usage. Please only specify a step or css.")
      } else
        println("Wrong usage. Please only specify a step or css.")
    }
  }

  /**
   * Prints everything about the current project as a listing of
   * all the steps and css classes so far.
   * "Give them nothing! But take from them everything!"
   */
  private def listEverything() {
    println("Current Project: " + Context.curPres.get.title)
    println("Located in workspace: " + Context.curWorkspace.get.homePath)
    Context.curPres.get.tell()
  }

}
