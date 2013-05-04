package EnvStructs

import Entities.{CSSClass, Step}
import scala.collection.mutable._
import scala.annotation.tailrec

//todo: add logic to rename project directory on title change
//todo: robustness, item names must be valid (according to w3c)
/**
 * Defines a project object.
 * @param homePath The path of the project's directory.
 * @param title The name of this project.
 * @param steps The set of steps of this project.
 * @param cssClasses The set of css classes of this project.
 */
class Project (val homePath:String,
               var title:String,
               val steps:ArrayBuffer[Step],
               val cssClasses:ArrayBuffer[CSSClass]) {
  /**
   * Adds a step.
   * @param args the arguments.
   *             see Step class for details.
   */
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

    @tailrec
    def findStep(id:String, curIndex:Int):Int = {
      if(curIndex == steps.size) -1
      else if(steps(curIndex).attributes.contains("id") &&
        steps(curIndex).attributes("id") == id)
        curIndex
      else
        findStep(id, curIndex+1)
    }
    findStep(id, 0)
  }

  /**
   * Finds the css class by name and returns it if found.
   * @param classname the class name to be searched.
   * @return Some(css class) else None.
   */
  def getCSSClass(classname:String):Option[CSSClass] = {
    cssClasses.find(cssClass => cssClass.name == classname)
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
    cssClasses.foreach(_.tell())
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
    for(entry <- cssClasses) {
      str+=(entry.toString()+System.lineSeparator())
    }
    str
  }

  /**
   * Returns the html of this presentation
   * as string when committing. Support for user defined head links
   * in future maybe.
   */
  def html():String = {
    val head = "<!doctype html><html><head>" +
      "<link href=\"notimpressed.css\" rel=\"stylesheet\"></head>" +
      "<body class=\"impress-not-supported background\">"
    val rest = "<script src=\"impress-mini.js\"></script>" +
               "<script>impress().init();</script>" +
               "</body></html>"
    val impressBody = {
      @tailrec
      def recAppend(s:String,elems:Traversable[Step]):String = {
        elems match {
          case rest if rest.isEmpty => s+"</div>" //why no mutable Nil?
          case _ => recAppend(s+elems.head.toHTMLString, elems.tail)
        }
      }
      recAppend("<div id=\"impress\">", steps)
    }
    head+impressBody+rest
  }
}