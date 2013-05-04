package FileIO

/**
 * Utilities for dealing with paths and file system locale.
 */
object PathUtils {
  val separator = System.getProperty("file.separator")

  /**
   * Replaces file separators to match locale.
   * @param path the path to normalize.
   * @return a processed path string with
   *         correct file separators.
   */
  def normalizePath(path: String): String =
    path.replace('/', '\\')


  /**
   * Creates the absolute path of a project.
   * @param projName name of the project.
   * @param wsPath path string of the workspace
   * @return the concatenation of the workspace path
   *         and the project name.
   */
  def toFullProjPath(projName:String, wsPath:String):String =
    wsPath+separator+projName
}
