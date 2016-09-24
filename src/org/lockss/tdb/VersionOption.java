/*

Copyright (c) 2000-2016, Board of Trustees of Leland Stanford Jr. University,
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package org.lockss.tdb;

import org.apache.commons.cli.*;

/**
 * <p>
 * Utilities defining a standard version option.
 * </p>
 * <p>
 * If the version option created by {@link #addOptions(Options)} is requested on
 * the command line,
 * <b>and then will exit with {@link System#exit(int)}</b>.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.68
 */
public class VersionOption {

  /**
   * <p>
   * Key for the version option ({@value}).
   * </p>
   * 
   * @since 1.68
   */
  protected static final String KEY_VERSION = "version";

  /**
   * <p>
   * Adds the standard version option to a Commons CLI {@link Options} instance.
   * </p>
   * 
   * @param opts
   *          A Commons CLI {@link Options} instance.
   * @since 1.68
   */
  public static void addOptions(Options opts) {
    opts.addOption(Option.builder()
                   .longOpt(KEY_VERSION)
                   .desc("show version information and exit")
                   .build());
  }

  /**
   * <p>
   * 
   * </p>
   * 
   * @param cmd
   * @param versionStrings
   * @since 1.68
   */
  public static void processCommandLine(CommandLineAccessor cmd,
                                        String... versionStrings) {
    if (cmd.hasOption(KEY_VERSION)) {
      StringBuilder sb = new StringBuilder();
      for (String versionString : versionStrings) {
        sb.append(versionString);
      }
      System.out.println(sb.toString());
      System.exit(0);
    }
  }
  
}
