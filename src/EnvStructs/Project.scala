package EnvStructs

import java.nio.file.Path
import Entities.{CSSClass, Step}
import scala.collection.mutable._

//todo: add logic to rename project directory on title change
//todo: robustness, item names must be valid (according to w3c)
class Project (val homePath:Path,
               var title:String,
               val steps:ArrayBuffer[Step],
               val cssRules:ArrayBuffer[CSSClass]) {

  def addStep(args:HashMap[String,String]) {
    args+=(("class",
      if(args.contains("class")) {
        if(!args("class").contains("step"))
          args("class")+" step"
        else
          args("class")
      } else "step" )) //all steps must have step as a class
    try{
      if(args.contains("at")){
        args("at").toInt match {
          case i if(i < steps.size && i > -1) =>
            steps.insert(i, new Step(args-("at")))
          case _ =>
            println("Position out of range. Adding at end.")
            steps+=(new Step(args-("at")))
        }
      } else
        steps+=(new Step(args))
    } catch {
      case _:Throwable => println("Invalid insert position.")
    }
  }

  /**
   * Gets the step given a key.
   * @param key a String representing an id or integer.
   * @return The step in steps identified by key if found.
   */
  def getStep(key:String):Option[Step] = {
    try{
      getStepByIndex(key.toInt)
    } catch {
      case _:Throwable =>
        getStepById(key)
    }
  }

  /**
   * Gets the step by given index.
   * @param idx The index of the step.
   * @return The step in step at index idx if found.
   */
  private def getStepByIndex(idx:Int):Option[Step] = {
    if(idx > steps.size - 1 || idx < 0){
      println("Step out of range. Current # of steps: " + steps.size)
      None
    }else Some(steps(idx))
  }

  /**
   * Gets the step by given id and returns that step.
   * @param id The id of the step to be found.
   * @return The step with matching id if found.
   */
  private def getStepById(id:String):Option[Step] = {
    getStepIndex(id) match {
      case -1 => None
      case i => Some(steps(i))
    }
  }

  /**
   * Finds the step by given id and returns its index.
   * @param id the id of the element
   * @return The index of the step if exists else -1.
   */
  private def getStepIndex(id:String):Int = {
    //todo: would there be use cases with a terabillion of steps?
    for(i <- 0 until steps.size
        if steps(i).attributes.contains("id") &&
          steps(i).attributes("id") == id) i
    -1
  }

  /**
   * Finds the css class by name and returns it if found.
   * @param classname the class name to be searched.
   * @return Some(css class) else None.
   */
  def getCSSClass(classname:String):Option[CSSClass] = {
    for(cssClass <- cssRules if cssClass.name == classname)
      Some(cssClass)
    println("No css class by name \"" + classname + "\" found.")
    None
  }

  /**
   * Prints a listing of this project's contents.
   */
  def tell() {
    println("Steps:")
    for(i <- 0 until steps.length){
      println("\tstep("+i+"):")
      steps(i).tell()
    }
    println("CSS Classes:")
    cssRules.foreach(_.tell())
  }
  //todo: make serialization less informal
  /**
   * For serialization notimpressed.pres file.
   * @return A string for this project's serialized form.
   */
  def serializedPres():String = {
    var str = "title:"+title+System.lineSeparator()
    for(step <- steps){
      str+=(step.toString()+System.lineSeparator())
    }
    str
  }

  /**
   * For serialization notimpressed.css files
   * @return
   */
  def serializedCSS():String = {
    var str = ""
    for(entry <- cssRules) {
      str+=(entry.toString()+System.lineSeparator())
    }
    str
  }

  /**
   * Returns the html of this presentation
   * as string when committing.
   */
  def html():String = {
    var str = "<div id=\"impress\">" + System.lineSeparator()
    for(step <- steps)
      str+=("\t"+step.toHTMLString + System.lineSeparator())
    str+"</div>"
  }
}
