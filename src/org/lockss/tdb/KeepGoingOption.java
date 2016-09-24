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

import java.util.*;

import org.apache.commons.cli.*;

/**
 * <p>
 * Utilities defining a standard keep-going option.
 * </p>
 * <p>
 * If the keep-going option created by {@link #addOptions(Options)} is requested
 * on the command line processed by
 * {@link #processCommandLine(Map, CommandLineAccessor)},
 * {@link #isKeepGoing(Map)} will return <code>true</code> to indicate it; one
 * can then add errors to the list of errors via
 * {@link #addError(Map, Exception)} and retrieve them later via
 * {@link #getErrors(Map)}.
 * </p>
 * 
 * @author Thib Guicherd-Callin
 * @since 1.67
 */
public class KeepGoingOption {

  /**
   * <p>
   * Prevent instantiation.
   * </p>
   * 
   * @since 1.67
   */
  private KeepGoingOption() {
    // Prevent instantiation
  }

  /**
   * <p>
   * Key for the standard input option ({@value}).
   * </p>
   * 
   * @since 1.67
   */
  protected static final String KEY_KEEP_GOING = "keep-going";
  
  /**
   * <p>
   * Single letter for the standard input option ({@value}).
   * </p>
   * 
   * @since 1.67
   */
  protected static final char LETTER_KEEP_GOING = 'k';
  
  /**
   * <p>
   * Key for the keep-going option's error list.
   * </p>
   * 
   * @since 1.67
   */
  protected static final String KEY_KEEP_GOING_ERRORS = KEY_KEEP_GOING + "__errors";
  
  /**
   * <p>
   * Adds the standard keep-going option to a Commons CLI {@link Options}
   * instance.
   * </p>
   * 
   * @param opts
   *          A Commons CLI {@link Options} instance.
   * @since 1.67
   */
  public static void addOptions(Options opts) {
    opts.addOption(Option.builder(Character.toString(LETTER_KEEP_GOING))
                   .longOpt(KEY_KEEP_GOING)
                   .desc(String.format("keep going past errors through all input files"))
                   .build());
  }

  /**
   * <p>
   * Processes a {@link CommandLineAccessor} instance and stores appropriate
   * information in the given options map.
   * </p>
   * 
   * @param options
   *          An options map.
   * @param cmd
   *          A {@link CommandLineAccessor} instance.
   * @since 1.67
   */
  public static void processCommandLine(Map<String, Object> options,
                                        CommandLineAccessor cmd) {
    options.put(KEY_KEEP_GOING, Boolean.valueOf(cmd.hasOption(KEY_KEEP_GOING)));
    options.put(KEY_KEEP_GOING_ERRORS, new ArrayList<Exception>());
  }

  /**
   * <p>
   * Determines from the options map if the keep-going option has been requested
   * on the command line.
   * </p>
   * 
   * @param options
   *          An options map.
   * @return <code>true</code> if and only if the keep-going option has been
   *         requested on the command line.
   * @since 1.67
   */
  public static boolean isKeepGoing(Map<String, Object> options) {
    Boolean keepGoing = (Boolean)options.get(KEY_KEEP_GOING);
    return keepGoing != null && keepGoing.booleanValue();
  }
  
  /**
   * <p>
   * Adds an error to the list kept internally in the options map and proceeds.
   * </p>
   * 
   * @param options
   *          The options map.
   * @param exc
   *          An exception.
   * @since 1.67
   */
  public static void addError(Map<String, Object> options,
                              Exception exc) {
    getErrors(options).add(exc);
  }
  
  /**
   * <p>
   * Retrieves the list of errors kept internally in the options map.
   * </p>
   * 
   * @param options
   *          The options map.
   * @return The list of errors stored in the options map.
   * @since 1.67
   */
  public static List<Exception> getErrors(Map<String, Object> options) {
    return (List<Exception>)options.get(KEY_KEEP_GOING_ERRORS);
  }

}
