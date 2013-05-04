package FileIO.Writers

import EnvStructs.Workspace
import java.nio.file.{StandardOpenOption, Files, Paths, Path}
import collection.JavaConversions
import java.nio.charset.StandardCharsets
import FileIO.PathUtils

/**
 * Handles serialization of workspace objects.
 */
object WorkspaceWriter {
  /**
   * Serializes a given workspace by writing to the
   * .ws file located in the directory of this workspace.
   * @param ws the workspace to be serialized.
   */
  def write(ws: Workspace) {
    //todo: add check for write only when changed
    if (ws.changed == true) {
      val wsFile: Path = Paths.get(ws.homePath.toString +
        PathUtils.normalizePath("/notimpressed.ws"))
      Files.write(wsFile,
        JavaConversions.asJavaIterable(ws.projList),
        StandardCharsets.UTF_8, StandardOpenOption.WRITE)
    }
  }
}