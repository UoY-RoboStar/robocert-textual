/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa - original code in RoboChart CSP generator
 *   Pedro Ribeiro - original code in RoboChart CSP Generator
 *   Matt Windsor - initial definition in RoboCert
 ********************************************************************************/

/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.BasicPackage
import circus.robocalc.robochart.NamedElement
import com.google.common.io.Files
import robocalc.robocert.generator.tockcsp.core.PathSet
import com.google.inject.Inject
import java.nio.file.Path

/**
 * Extensions for locating the CSP definition filenames for things.
 * 
 * @author Matt Windsor
 */
class FilenameExtensions {
	@Inject PathSet ps;
	
	/**
	 * Gets the path (relative to the CSP root) of this package's 'defs' file.
	 * 
	 * @param it  the package whose file is required.
	 * 
	 * @return the package's defs file.
	 */
	def getCSPDefsFileName(BasicPackage it) '''«ps.DEFS_FROM_PACKAGE_PATH»/«fileBasename»_defs.csp'''

	/**
	 * Gets the path (relative to the CSP root) of this package's main file.
	 * 
	 * @param it  the package whose file is required.
	 * 
	 * @return the package's defs file.
	 */
	def getCSPMainFileName(BasicPackage it) '''«ps.DEFS_FROM_PACKAGE_PATH»/«fileBasename».csp'''

	/**
	 * Gets the RoboChart basename for a package.
	 * 
	 * @param it  the package under consideration.
	 * 
	 * @return the package's basename, derived either from its declared name or
	 *         (if no name exists) its filename.
	 */
	def getFileBasename(BasicPackage it) {
		// from GeneratorUtils
		name?.basename ?: "file_" + Files.getNameWithoutExtension(eResource.URI.lastSegment)
	}

	/**
	 * Gets the path (relative to the CSP root) of this element's top module's
	 * main CSP definition file.
	 * 
	 * @param it  the element whose file is required.
	 * 
	 * @return the package's defs file.
	 */
	def getCSPTopModuleFileName(NamedElement it) '''«ps.DEFS_FROM_PACKAGE_PATH»/«topModuleFileBasename».csp'''
	
	private def CharSequence getTopModuleFileBasename(NamedElement it) {
		switch parent: eContainer {
			BasicPackage: (parent?.name ?: name).basename
			NamedElement: parent.topModuleFileBasename
		}
	}
	
	private def getBasename(String it) {
		// from GeneratorUtils
		it?.replaceAll("::", "_")
	}
	
	/**
	 * @return whether the path denotes a RoboChart file.
	 */
	def isRoboChartFile(Path p) {
		extensionEquals("rct", p)
	}
	
	/**
	 * @return whether the path denotes a RoboCert file.
	 */
	def isRoboCertFile(Path p) {
		extensionEquals("rcert", p)
	}
	
	private def extensionEquals(String expected, Path p) {	
		expected == Files.getFileExtension(p.toString)
	}
}