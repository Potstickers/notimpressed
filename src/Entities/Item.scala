package Entities

import scala.collection.mutable.HashMap

/**
 * Not used yet.
 * Defines an entity at bare minimum.
 * cssclass and step will extend form this.
 * @param attributes
 */
abstract class Item (val attributes:HashMap[String,String]){
  /*todo: Generify CSSClass and Step classes.
    All shall have at minimum:
      Fields:
        a map for attribute,value pairs
      Methods:
        set/update method
        delete method
        tell method
   */
  def set(attribute:String, value:String){
    attributes+=((attribute,value))
  }
  def remove(attribute:String){
    attributes-=(attribute)
  }
  def tell()
}
