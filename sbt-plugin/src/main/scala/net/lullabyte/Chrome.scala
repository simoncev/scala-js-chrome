package net.lullabyte

import chrome.Impl.{App, AppManifest, ExtensionManifest, Background}
import chrome.Manifest
import chrome.permissions.{APIPermission, HostPermission, Permission}
import sbt._

object Chrome {

  val mainFileName = "main.js"
  val dependenciesFileName = "dependencies.js"
  val launcherFileName = "launcher.js"
  val defaultScripts = List(dependenciesFileName, mainFileName, launcherFileName)

  def i18n(msg: String): String = s"__MSG_${msg}__"

  def icons(base: String, name: String, sizes: Set[Int]): Map[Int, String] = {
    sizes.map{ size =>
      size -> s"$base/$size/$name"
    }.toMap
  }

  def buildUnpackedDirectory(unpacked: File)(manifest: File, jsLib: File,
                                             jsDeps: File, jsLauncher: File, resources: Seq[File]): File =  {
    IO.createDirectory(unpacked)
    resources.foreach { resource =>
      IO.copyDirectory(resource, unpacked, overwrite = true, preserveLastModified = true)
    }
    IO.copy(List(
      (jsLib, unpacked / mainFileName),
      (jsDeps, unpacked / dependenciesFileName),
      (jsLauncher, unpacked / launcherFileName),
      (manifest, unpacked / "manifest.json")
    ), overwrite = true, preserveLastModified = true)
    unpacked
  }

  def generateManifest(out: File)(manifest: Manifest): File = {
    import Pickler._
    val content = upickle.default.write(manifest)
    IO.write(out, content)
    out
  }
}
