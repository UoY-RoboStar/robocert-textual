/*
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */

package robostar.robocert.textual.generator.tockcsp.core;

/**
 * Contains the paths into which tock-CSP files will be generated.
 *
 * <p>Both import generation and top-level file placement depend on knowing the relationship
 * between these paths and each other as well as the RoboChart files.
 *
 * @author Matt Windsor
 */
public class TockCspPathSet {
  // TODO(@MattWindsor91): this might not work on eg. Windows.

  /**
   * Directory, relative to the RoboStar modelling project, where we expect RoboChart to output
   * tock-CSP.
   */
  private final String ROBOCHART_CSP_PATH = "./csp-gen/timed";

  /**
   * Directory, relative to the RoboStar modelling project, where we root all of the RoboCert
   * tock-CSP.
   */
  private final String BASE_PATH = ROBOCHART_CSP_PATH + "/robocert";

  /**
   * Directory, relative to the RoboStar modelling project, in which we generate package CSP files.
   */
  public final String PACKAGE_PATH = BASE_PATH + "/pkg";

  /**
   * Directory, relative to BASE_PATH, in which we copy standard-library CSP files.
   */
  private final String LIBRARY_EXT = "/lib";

  /**
   * Directory, relative to the RoboStar modelling project, in which we copy standard-library CSP
   * files.
   */
  public final String LIBRARY_PATH = BASE_PATH + LIBRARY_EXT;

  //
  // Relative to package path
  //

  /**
   * Directory, relative to CSP_PACKAGE_PATH, in which we can find the base path.
   */
  private final String BASE_FROM_PACKAGE_PATH = "..";

  /**
   * Directory, relative to CSP_PACKAGE_PATH, in which we can find the RoboChart CSP path.
   */
  public final String ROBOCHART_FROM_PACKAGE_PATH = BASE_FROM_PACKAGE_PATH + "/..";

  /**
   * Directory, relative to CSP_PACKAGE_PATH, in which we can find RoboChart definitions files.
   *
   * <p>Note that the standard library files have a similar relative path hardcoded in the .csp
   * files, which must be kept up to date with this.
   */
  public final String DEFS_FROM_PACKAGE_PATH = ROBOCHART_FROM_PACKAGE_PATH + "/defs";

  /**
   * Directory, relative to CSP_PACKAGE_PATH, in which we can find library files.
   *
   * <p>Note that the standard library files have a similar relative path hardcoded in the .csp
   * files, which must be kept up to date with this.
   */
  public final String LIBRARY_FROM_PACKAGE_PATH = BASE_FROM_PACKAGE_PATH + LIBRARY_EXT;

  /**
   * Constructs an include path for a RoboChart CSP definitions file.
   *
   * @param basename base name of the CSP file.
   * @return the path, relative to CSP_PACKAGE_PATH, of the file.
   */
  public String include(String basename) {
    return "%s/%s.csp".formatted(DEFS_FROM_PACKAGE_PATH, basename);
  }
}
