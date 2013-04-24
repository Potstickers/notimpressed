package FileIO

object PathUtils {
  val separator = System.getProperty("file.separator")

  def normalizePath(path: String): String = {
    if (separator == "\\") path.replace('/', '\\') else path
  }

  def toFullProjPath(projName:String, wsPath:String):String = {
    wsPath+separator+projName
  }
}
