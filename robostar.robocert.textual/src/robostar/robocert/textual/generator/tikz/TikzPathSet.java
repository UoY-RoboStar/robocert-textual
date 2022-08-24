/*******************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robostar.robocert.textual.generator.tikz;

/**
 * Contains the paths into which tock-CSP files will be generated.
 *
 * <p>Both import generation and top-level file placement depend on knowing the relationship between
 * these paths and each other as well as the RoboChart files.
 *
 * @author Matt Windsor
 */
public class TikzPathSet {
  // TODO(@MattWindsor91): this might not work on eg. Windows.

  /**
   * Directory, relative to the RoboStar modelling project, in which we make the TikZ development.
   */
  public final String BASE_PATH = "./tikz-gen";

  /**
   * Directory, relative to the RoboStar modelling project, in which we generate TikZ diagrams.
   */
  public final String DIAGRAM_PATH = BASE_PATH + "/diagrams";


  /**
   * Directory, relative to the RoboStar modelling project, in which we copy standard-library TikZ
   * files.
   */
  public final String LIBRARY_PATH = BASE_PATH + "/lib";
}