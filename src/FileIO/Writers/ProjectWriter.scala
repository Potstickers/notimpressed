package FileIO.Writers

import EnvStructs.Project
import java.nio.file.{Files, Paths}
import FileIO.PathUtils

/**
 * Handles serialization of Project objects.
 */
object ProjectWriter {
  /**
   * Serializes a given project by
   * writing the .pres and .css files in the project directory.
   * @param proj the proj to be serialized.
   */
  def write(proj:Project){
    //todo: write only when modified
    val homePathString = proj.homePath
    val presFile = Paths.get(homePathString+
      PathUtils.normalizePath("/notimpressed.pres"))
    val cssFile = Paths.get(homePathString+
      PathUtils.normalizePath("/notimpressed.css"))
    Files.write(presFile, proj.serializedPres().getBytes)
    Files.write(cssFile, proj.serializedCSS().getBytes)
  }

  /**
   * Writes the given project as html file.
   * @param proj the project to convert to html file.
   */
  def writePresHTML(proj:Project){
    val homePathString = proj.homePath
    val htmlFile = Paths.get(homePathString+
      PathUtils.normalizePath("/presentation.html"))
    Files.write(htmlFile, proj.html().getBytes)
  }
}
