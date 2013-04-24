package Command

import collection.mutable.HashMap
import EnvStructs.{States, Context}

/**
 * Does exactly what the class name says.
 */
object CmdDelegator {

  def execute(cmdArgs: (String, HashMap[String, String])) = {
    Context.currentState match {
      case States.Main => MainExecutor.execute(cmdArgs)
      case States.Proj => ProjExecutor.execute(cmdArgs)
    }
  }
}
