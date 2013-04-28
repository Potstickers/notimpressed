package Entities

import collection.mutable.HashMap

class CSSClass(var name:String,
               attributes:HashMap[String, String]) {

  def set(avPair:(String,String)){
    attributes+=(avPair)
  }
  def remove(attribute:String){
    attributes-=(attribute)
  }
  def tell(){
    println("\tclass name: " + name)
    attributes.foreach(
      kv => println("\t\t"+kv._1 + ": " + kv._2)
    )
  }
  override def toString:String = {
    var str = "."+name+"{"
    for(entry <- attributes)
      str+=(entry._1+":"+entry._2+";")
    str+'}'
  }
}
