/* Copyright (C) 2021-2025
 * Julian Valentin, Daniel Spitzer, lsp-cli-plus Development Community
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package org.bsplines.lspcli.tools

import java.nio.file.Paths
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileIoTest {
  @Test
  fun testGetCodeLanguageIdFromPathElisp() {
    // Regression: .el used to fall through to null and got plain-text-checked,
    // flagging every elisp identifier as a misspelling.
    assertEquals("elisp", FileIo.getCodeLanguageIdFromPath(Paths.get("init.el")))
    assertEquals(
      "elisp",
      FileIo.getCodeLanguageIdFromPath(Paths.get("/Users/me/.config/emacs/lsp-core.el")),
    )
  }

  @Test
  fun testGetCodeLanguageIdFromPathElispNeighbours() {
    // The .el branch sits between .ex (elixir) and .elm (elm); confirm that
    // a shared prefix doesn't shadow either neighbour.
    assertEquals("elixir", FileIo.getCodeLanguageIdFromPath(Paths.get("module.ex")))
    assertEquals("elm", FileIo.getCodeLanguageIdFromPath(Paths.get("Main.elm")))
  }

  @Test
  fun testGetCodeLanguageIdFromPathRepresentativeExtensions() {
    assertEquals("c", FileIo.getCodeLanguageIdFromPath(Paths.get("foo.c")))
    assertEquals("cpp", FileIo.getCodeLanguageIdFromPath(Paths.get("foo.cpp")))
    assertEquals("java", FileIo.getCodeLanguageIdFromPath(Paths.get("Foo.java")))
    assertEquals("kotlin", FileIo.getCodeLanguageIdFromPath(Paths.get("Foo.kt")))
    assertEquals("python", FileIo.getCodeLanguageIdFromPath(Paths.get("foo.py")))
    assertEquals("lisp", FileIo.getCodeLanguageIdFromPath(Paths.get("foo.lisp")))
    assertEquals("clojure", FileIo.getCodeLanguageIdFromPath(Paths.get("foo.clj")))
    assertEquals("markdown", FileIo.getCodeLanguageIdFromPath(Paths.get("README.md")))
    assertEquals("latex", FileIo.getCodeLanguageIdFromPath(Paths.get("paper.tex")))
  }

  @Test
  fun testGetCodeLanguageIdFromPathUnknown() {
    assertNull(FileIo.getCodeLanguageIdFromPath(Paths.get("foo.unknownext")))
    assertNull(FileIo.getCodeLanguageIdFromPath(Paths.get("noextension")))
  }
}
