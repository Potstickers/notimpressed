package FileIO.Writers

import EnvStructs.Workspace
import java.nio.file.{StandardOpenOption, Files, Paths, Path}
import collection.JavaConversions
import java.nio.charset.StandardCharsets
import FileIO.PathUtils

object WorkspaceWriter {
  def write(ws:Workspace) = {
    //todo: add check for write only when changed
    val wsFile:Path = Paths.get(ws.homePath.toString +
      PathUtils.normalizePath("/notimpressed.ws"))
    Files.write(wsFile,JavaConversions.asJavaIterable(ws.projList),
      StandardCharsets.UTF_8, StandardOpenOption.WRITE)
  }
}
