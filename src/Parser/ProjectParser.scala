package Parser

import scala.collection.mutable.{HashMap, ArrayBuffer}
import Entities.{CSSClass, Step}
import java.nio.file.{Files, Path}
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._

/**
 * Parser for everything involved in
 * parsing project directory related files.
 */
object ProjectParser {

  def parsePres(path: Path): (String, ArrayBuffer[Step]) = {
    val lines = Files.readAllLines(path, StandardCharsets.UTF_8).asScala
    (parseTitle(lines.head), parseSteps(lines.drop(1)))
  }
  //todo: very similar to parseSteps, do something about it.
  def parseCSSRules(path:Path): ArrayBuffer[CSSClass] = {
    val rules = Files.readAllLines(path, StandardCharsets.UTF_8).asScala
    val buffer = new ArrayBuffer[CSSClass]()
    buffer ++= (for (rule <- rules) yield parseCSS(rule))
  }
  private def parseTitle(line: String): String = {
    line.substring(line.indexOf(':') + 1)
  }

  private def parseSteps(steps:Traversable[String]): ArrayBuffer[Step] = {
    val buffer = new ArrayBuffer[Step]()
    buffer ++= (for (line <- steps) yield parseStep(line))
  }

  private def parseStep(line: String): Step = {
    val idxOfOpenBrkt = line.indexOf('{')
    val idxOfCloseBrkt = line.lastIndexOf('}')
    val attributes = new HashMap[String, String]()
    val splitted = line.substring(idxOfOpenBrkt + 1, idxOfCloseBrkt).split(';')
    for (av <- splitted) {
      val splitAV = av.split(':')
      attributes+=((splitAV(0), splitAV(1)))
    }
    new Step(attributes)
  }
  //todo: very similar to parseStep, do something about it.
  /**
   * Parses a css class and returns an object representing it.
   * @param cssclass The string of the css class to be parsed
   * @return CSSClass
   */
  private def parseCSS(cssclass: String):CSSClass = {
    val idxOfOpenBrkt = cssclass.indexOf('{')
    val idxOfCloseBrkt = cssclass.lastIndexOf('}')
    val className = cssclass.substring(1,idxOfOpenBrkt)
    val attributes = new HashMap[String, String]()
    val splitted = cssclass.substring(
      idxOfOpenBrkt+1,
      idxOfCloseBrkt).split(';')
    for (av <- splitted) {
      val splitAV = av.split(':')
      attributes += ((splitAV(0), splitAV(1)))
    }
    new CSSClass(className,attributes)
  }
}
