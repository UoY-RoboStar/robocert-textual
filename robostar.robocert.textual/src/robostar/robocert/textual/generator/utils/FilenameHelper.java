/*
 * Copyright (c) 2019-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.utils;

import com.google.common.io.Files;
import java.util.Objects;
import java.nio.file.Path;

/**
 * Extensions pertaining to filenames.
 *
 * @author Alvaro Miyazawa (initial definition in RoboChart)
 * @author Pedro Ribeiro (initial definition in RoboChart)
 * @author Matt Windsor (port to RoboCert)
 */
public class FilenameHelper {

  /**
   * Whether the path denotes a RoboChart file.
   */
  public boolean isRoboChartFile(Path p) {
    return extensionEquals("rct", p);
  }

  /**
   * Whether the path denotes a RoboCert file.
   */
  public boolean isRoboCertFile(Path p) {
    return extensionEquals("rcert", p);
  }

  private boolean extensionEquals(String expected, Path p) {
    //noinspection UnstableApiUsage
    return Objects.equals(expected, Files.getFileExtension(p.toString()));
  }
}
