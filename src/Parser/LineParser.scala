package Parser

import collection.mutable._

object LineParser {
  val regex: String = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)"

  def parse(line: String): Option[(String, HashMap[String, String])] = {
    val tokens: Array[String] = line.split(regex)
    if (tokens.length < 1) None
    else Some((tokens(0), argsToMap(tokens.drop(1))))
  }

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

  private def getNameValuePair(arg: String): (String, String) = {
    val s = arg.trim
    val colonIndex = s.indexOf(':')
    (s.substring(0, colonIndex).trim,
      s.substring(
        s.indexOf('\"', colonIndex) + 1,
        s.lastIndexOf('\"')).trim)
  }

}
