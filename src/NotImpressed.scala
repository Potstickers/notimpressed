import Command.CmdDelegator
import EnvStructs.{States, Context}
import Parser._

/**
 * Main command loop.
 */
object NotImpressed {

  private def prompt() {
    Context.state match {
      case States.Main => print("@"+{
        if(Context.workspace.isDefined)
          Context.workspace.get.wsName
        else
          "\\Unspecified\\"
      } + "> ")
      case States.Proj => print("@"+
        Context.workspace.get.wsName +
        "["+Context.pres.get.title + "]> ")
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
