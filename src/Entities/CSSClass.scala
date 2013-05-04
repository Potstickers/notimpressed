package Entities

import collection.mutable.HashMap

/**
 * Defines a css class object.
 * @param name the name of the css class.
 * @param attributes css attributes for this class.
 */
class CSSClass(var name:String,
               attributes:HashMap[String, String]) {
  /**
   * Updates the attributes map with the
   * given (attribute,value) pair.
   * @param avPair (attribute,value) pair.
   */
  def set(avPair:(String,String)){
    attributes+=(avPair)
  }

  /**
   * Removes an attribute from the attributes map.
   * @param attribute The attribute(/key) to remove.
   */
  def remove(attribute:String){
    attributes-=(attribute)
  }

  /**
   * Prints information about this class as
   * listing of its name, and all its attributes and values.
   */
  def tell(){
    println("\tclass name: " + name)
    attributes.foreach(
      kv => println("\t\t"+kv._1 + ": " + kv._2)
    )
  }

  /**
   * Intended for serialization.
   * Same way classes are handled in actual css.
   * @return String to be serialized.
   */
  override def toString:String = {
    var str = "."+name+"{"
    for(entry <- attributes)
      str+=(entry._1+":"+entry._2+";")
    str+'}'
  }
}
