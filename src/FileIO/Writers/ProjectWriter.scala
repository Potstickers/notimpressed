package FileIO.Writers

import EnvStructs.Project
import java.nio.file.{Files, Paths}
import FileIO.PathUtils

object ProjectWriter {

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

  def writePresHTML(proj:Project){
    val homePathString = proj.homePath
    val htmlFile = Paths.get(homePathString+
      PathUtils.normalizePath("/generatedSnip.html"))
    Files.write(htmlFile, proj.html().getBytes)
  }
}
