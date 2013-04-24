import Command.CmdDelegator
import EnvStructs.{States, Context}
import Parser._

object NotImpressed {

  private def prompt() {
    Context.currentState match {
      case States.Main => print("@"+
        Context.curWorkspace.get.wsName + "> ")
      case States.Proj => print("@"+
        Context.curWorkspace.get.wsName +
        "["+Context.curPres.get.title + "]> ")
    }
  }

  private def init() {
    initPrompt
    //and some more stuff as needed
  }

  private def initPrompt() {
    println("Welcome to NotImpressed!")
    print("To get started, ")
    println("type \"new name:\"your project name\" in:\"project path\"")
    println("or type \"open name:\"project name\" in:\"project path\"")
  }
  /*
  private def printCmdandArgs(o: Option[(String, HashMap[String, String])]) {
    val parsed = o.get
    print("Cmd: " + parsed._1 + " Args: ")
    parsed._2.foreach((kv) => print("(" + kv._1 + "," + kv._2 + ")"))
    println
  }*/

  def main(args: Array[String]) {
    init
    while (true) {
      prompt
      val cmdAndArgs = LineParser.parse(readLine())
      val cmd = cmdAndArgs.get._1
      val args = cmdAndArgs.get._2
      CmdDelegator.execute(cmd, args)
    }
  }

}
