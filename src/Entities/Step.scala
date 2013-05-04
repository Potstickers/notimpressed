package Entities

import collection.mutable.HashMap

/**
 * Defines step object
 * @param attributes The set of attributes of this step.
 *                   Permissible attributes:
 *                   {id, class, data-x, data-y, data-z, data-scale, data-rotate,
 *                   data-rotate-x, data-rotate-y, content}.
 *                   Everything other than content are valid html attributes.
 *                   The data-attributes are html5 specific and impress.js specific.
 *                   Other than translating to html,
 *                   the data-attributes could drop the "data-" part.
 */
class Step(val attributes: HashMap[String, String]) {

  /**
   * Sets the given attribute to value
   * @param avPair The (attribute,value) pair to be updated
   */
  def set(avPair: (String, String)) {
    attributes += (avPair)
  }

  /**
   * Prints a list of attribute, value pairs.
   */
  def tell() {
    attributes.foreach(
      kv => println("\t\t" + kv._1 + ": " + kv._2)
    )
  }

  /**
   * For cerealization.
   * @return A knarly, far-out! 8) string ready to be serialized!!
   */
  override def toString: String = {
    var str = "step{"
    for (entry <- attributes)
      str += (entry._1 + ":" + entry._2 + ";")
    str + "}"
  }

  /**
   * The html representation of this step.
   * @return html string of this step.
   */
  def toHTMLString: String = {
    var str = "<div"
    for (entry <- attributes if entry._1 != "content") {
      str += (' ' + {
        //i love these things.
        entry._1 match {
          case "x" | "y" | "z" | "rotate" |
               "rotate-x" | "rotate-y" | "scale" =>
            "data-" + entry._1
          case _ => entry._1
        }
      } + "=\"" + entry._2 + '\"')
    }
    str + ('>' + "\t" +
      attributes.getOrElse("content", "") + "</div>")
  }
}
