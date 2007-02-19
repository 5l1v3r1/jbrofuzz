/**
 * DConstructor.java 0.5
 *
 * Java Bro Fuzzer. A stateless network protocol fuzzer for penetration tests.
 * It allows for the identification of certain classes of security bugs, by
 * means of creating malformed data and having the network protocol in question
 * consume the data.
 *
 * Copyright (C) 2007 subere (at) uncon org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.owasp.jbrofuzz.dir;

import org.owasp.jbrofuzz.io.FileHandler;
import org.owasp.jbrofuzz.ver.Format;
import org.owasp.jbrofuzz.JBroFuzz;

/**
 * Class used for constructing the contents of the text displayed within the
 * web directories panel. The necessity of this class lies within having the
 * ability to report errors to the main GUI log panel (system)
 *
 * @author subere (at) uncon org
 * @version 0.5
 */
public class DConstructor {
  /**
   * The main constructor reading the contents of the web directory file, if
   * one is present and setting the text within the panel.
   *
   * @param mJBroFuzz JBroFuzz
   */
  public DConstructor(JBroFuzz mJBroFuzz) {
    StringBuffer dirTextBuffer = FileHandler.readDirectories(Format.FILE_DIR);

    mJBroFuzz.getFrameWindow().getWebDirectoriesPanel().setDirectoriesText(
      dirTextBuffer);
  }
}