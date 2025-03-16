/* Copyright (C) 2021-2025
 * Julian Valentin, Daniel Spitzer, lsp-cli-plus Development Community
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.bsplines.lspcli

import org.bsplines.lspcli.testtools.TestTools
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class LspCliLauncherTest {
  @BeforeTest
  fun setUp() {
    /* Prevent warning message "Corrupted channel by directly writing to native stream in forked JVM 1"
    <forkCount>0</forkCount> removes the warning message, but disables jacoco code coverage checks
     */
    System.setOut(TestTools.getPrintStream())

    TestTools.downloadAndExtractLanguageServer(TestTools.getGitHubURL())
  }

  @Test
  fun testNoSpellingMistakes() {
    val testfile = TestTools.createTextFile("This is a test.", "latex-1.tex")
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--verbose",
          testfile,
        ),
      )
    assertEquals(0, returncode)
  }

  @Test
  fun testSpellingMistakes() {
    val testfile = TestTools.createTextFile("This is an test.", "typst-1.typ")
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--verbose",
          testfile,
        ),
      )
    assertEquals(3, returncode)
  }

  @Test
  fun testArguments() {
    val testfile = TestTools.createTextFile("This is a test.", "latex-3.tex")
    val clientConfig =
      TestTools.createTextFile(
        """{"latex": {"commands": "\\label{}"}}""",
        "config.json",
      )
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--server-working-directory",
          TestTools.getCurrentDirectory(),
          "--client-configuration",
          clientConfig,
          "--verbose",
          "--hide-commands",
          testfile,
        ),
      )
    assertEquals(0, returncode)
  }

  @Test
  fun testCheckFilesInDirectory() {
    val subdir = "test-dir"
    TestTools.createTextFile("This is a test.", "typst-2.typ", subdir)
    TestTools.createTextFile("This is a test.", "markdown-1.md", subdir)
    TestTools.createTextFile("This is an test.", "text-1.txt", subdir)
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--verbose",
          TestTools.getPathtoSubDir(subdir),
        ),
      )
    assertEquals(3, returncode)
  }

  @Test
  fun testInvalidParameter() {
    val testfile = TestTools.createTextFile("This is a test.", "typst-3.typ")
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--verbose",
          "--invalidArgument",
          testfile,
        ),
      )
    assertEquals(2, returncode)
  }

  @Test
  fun testMissingMandatoryArgument() {
    val testfile = TestTools.createTextFile("This is a test.", "typst-4.typ")
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--verbose",
          testfile,
        ),
      )
    assertEquals(2, returncode)
  }

  @Test
  fun testUnknownFile() {
    val returncode =
      LspCliLauncher.run(
        arrayOf(
          "--server-command-line",
          TestTools.getExecutable(),
          "--verbose",
          "unknownFile.md",
        ),
      )
    assertEquals(1, returncode)
  }

  @Test
  fun testRequestHelpAndVersion() {
    var returncode =
      LspCliLauncher.run(
        arrayOf(
          "--help",
        ),
      )
    assertEquals(0, returncode)

    returncode =
      LspCliLauncher.run(
        arrayOf(
          "--version",
        ),
      )
    assertEquals(0, returncode)
  }
}
