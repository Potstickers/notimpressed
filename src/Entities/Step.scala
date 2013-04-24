package Entities

import collection.mutable.HashMap

class Step (val attributes:HashMap[String,String]){

  /**
   * Sets the given attribute to value
   * @param avPair The (attribute,value) pair to be updated
   */
  def set(avPair:(String,String)) {
    attributes+=(avPair)
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
  override def toString:String = {
    var str = "step{"
    for(entry <- attributes)
      str+=(entry._1+":"+entry._2+";")
    str+"}"
  }

  def toHTMLString:String = {
    var str = "<div"
    for(entry <- attributes if entry._1 != "content") {
      str+=(' '+entry._1+"=\""+entry._2+'\"')
    }
    str+('>'+ System.lineSeparator() + "\t" +
      attributes.getOrElse("content", "") +
      System.lineSeparator()+"</div>")
  }
}
