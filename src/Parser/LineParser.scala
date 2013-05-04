package Parser

import collection.mutable._

/**
 * Line parser object. Handles breaking up user input.
 */
object LineParser {
  private val regex: String = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)"

  /**
   * The parse function. The first word of input is the command.
   * the rest are <key>:"<value>"... all delimited by space.
   * @param line line of input
   * @return a (cmd, args) pair
   */
  def parse(line: String): Option[(String, HashMap[String, String])] = {
    val tokens: Array[String] = line.split(regex)
    if (tokens.length < 1) None
    else Some((tokens(0), argsToMap(tokens.drop(1))))
  }

  /**
   * Given an array of strings of form: <attribute>:"<value>".
   * puts into map of where <attribute> => "<value>".
   * @param args the list of attribute value strings.
   * @return mapping of attribute to values.
   */
  private def argsToMap(args: Array[String]): HashMap[String, String] = {
    val map: HashMap[String, String] = new HashMap[String, String]()
    for (arg <- args) {
      val kv = getNameValuePair(arg.trim)
      map += (kv._1 -> kv._2)
    }
    map
  }

  /* todo: add logic for detecting context.
  * In project state, if editing css, tokenize on :, if step, tokenize on =*/
  /**
   * Breaks a string of form <attribute>:"<value>" into
   * (<attribute>,"<value>") pairs.
   * @param arg the attribute value string
   * @return (attribute,value) pair
   */
  private def getNameValuePair(arg: String): (String, String) = {
    val s = arg.trim
    val colonIndex = s.indexOf(':')
    (s.substring(0, colonIndex).trim,
      s.substring(
        s.indexOf('\"', colonIndex) + 1,
        s.lastIndexOf('\"')).trim)
  }

}
