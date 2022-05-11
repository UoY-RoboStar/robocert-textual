/********************************************************************************
 * Copyright (c) 2019-2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa - initial implementation in RoboChart CSP generator
 *   Pedro Ribeiro - initial implementation in RoboChart CSP generator
 *   Matt Windsor - port to RoboCert
 ********************************************************************************/

package robostar.robocert.textual.generator.utils;

import com.google.common.io.Files;
import java.util.Objects;
import java.nio.file.Path;

/**
 * Extensions pertaining to filenames.
 * 
 * @author Matt Windsor
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
