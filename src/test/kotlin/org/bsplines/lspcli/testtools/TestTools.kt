/* Copyright (C) 2021-2025
 * Julian Valentin, Daniel Spitzer, lsp-cli-plus Development Community
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.bsplines.lspcli.testtools

import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.net.HttpURLConnection
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

object TestTools {
  private const val OUTPUTDIR = "target"
  private const val LTEXLSPLUS = "ltex-ls-plus"
  private const val LTEXLSPLUSVERSION = "18.4.0"

  enum class OS(
    val lowercaseName: String,
  ) {
    WINDOWS("windows"),
    MAC("mac"),
    LINUX("linux"),
  }

  fun getPrintStream(): PrintStream {
    val file = File(Paths.get(OUTPUTDIR, "output-stream.txt").toUri())
    return PrintStream(file)
  }

  fun getGitHubURL(): String {
    var arch: String
    var fileExtension: String
    if (System.getProperty("os.arch") == "aarch64") {
      arch = "aarch64"
    } else {
      arch = "x64"
    }
    if (getOs() == OS.WINDOWS) {
      fileExtension = ".zip"
    } else {
      fileExtension = ".tar.gz"
    }

    return "https://github.com/ltex-plus/" + LTEXLSPLUS + "/releases/download/" +
      LTEXLSPLUSVERSION +
      "/" + LTEXLSPLUS + "-" +
      LTEXLSPLUSVERSION +
      "-" +
      getOs().lowercaseName +
      "-" +
      arch +
      fileExtension
  }

  fun getExecutable(): String {
    var fileExtension = ""
    if (getOs() == OS.WINDOWS) {
      fileExtension = ".bat"
    }
    return Paths
      .get(
        OUTPUTDIR,
        LTEXLSPLUS + "-" + LTEXLSPLUSVERSION,
        "bin",
        LTEXLSPLUS + fileExtension,
      ).toString()
  }

  fun getCurrentDirectory(): String = System.getProperty("user.dir")

  fun downloadAndExtractLanguageServer(url: String) {
    var zipFilePath = ""
    val index = url.lastIndexOf('/')

    if (index != -1) {
      var zipFileName = url.substring(index + 1, url.length)
      zipFilePath = Paths.get(OUTPUTDIR, zipFileName).toString()
    }

    if (!File(getExecutable()).exists()) {
      println("File " + getExecutable() + " does not exist. Start downloading...")
      downloadFile(url, zipFilePath)
      extractTarZip(zipFilePath)
    } else {
      println("File " + getExecutable() + " already exists. Skipping download.")
    }
  }

  fun getPathtoSubDir(subdir: String): String = Paths.get(OUTPUTDIR, subdir).toString()

  fun createTextFile(
    content: String,
    fileName: String,
    subdirectory: String = "",
  ): String {
    if (subdirectory != "") {
      Files.createDirectories(Paths.get(OUTPUTDIR, subdirectory))
      return Files.writeString(Paths.get(OUTPUTDIR, subdirectory, fileName), content).toString()
    } else {
      return Files.writeString(Paths.get(OUTPUTDIR, fileName), content).toString()
    }
  }

  private fun downloadFile(
    url: String,
    filePath: String,
  ) {
    println("Download file from $url")
    val connection = URI(url).toURL().openConnection() as HttpURLConnection
    connection.requestMethod = "GET"
    connection.connectTimeout = 15000
    connection.readTimeout = 15000

    connection.inputStream.use { input ->
      FileOutputStream(filePath).use { output ->
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
          output.write(buffer, 0, bytesRead)
        }
      }
    }
    println("File downloaded to " + filePath)
  }

  private fun extractTarZip(tarGzFilePath: String) {
    println("Extraction started.")
    val process =
      ProcessBuilder("tar", "-xzf", tarGzFilePath, "-C", OUTPUTDIR).start()
    val exitCode = process.waitFor()
    if (exitCode != 0) {
      println("Error extracting $tarGzFilePath. Exit code: $exitCode")
    } else {
      println("Extraction successful.")
    }
  }

  private fun getOs(): OS {
    if (System.getProperty("os.name").startsWith("Windows")) {
      return OS.WINDOWS
    } else if (System.getProperty("os.name").startsWith("Mac")) {
      return OS.MAC
    } else {
      return OS.LINUX
    }
  }
}
