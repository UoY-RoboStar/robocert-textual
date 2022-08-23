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
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robostar.robocert.textual.generator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;

import com.google.inject.Inject;

import robostar.robocert.textual.generator.tockcsp.core.PathSet;

/**
 * Overrides output configuration to save CSP to csp-gen.
 */
public class RoboCertOutputConfigurationProvider implements IOutputConfigurationProvider {
	@Inject
	private PathSet ps;

	@Override
	public Set<OutputConfiguration> getOutputConfigurations() {
		// TODO: is this the right thing to do?
		// TODO: PRISM gen

		final var set = new HashSet<OutputConfiguration>(3);
		set.add(buildConfig(IFileSystemAccess.DEFAULT_OUTPUT, "tock-CSP folder", ps.CSP_PACKAGE_PATH));
		set.add(buildConfig(CSP_LIBRARY_OUTPUT, "tock-CSP standard library", ps.CSP_LIBRARY_PATH));
		set.add(buildConfig(TIKZ_OUTPUT, "TikZ folder", ps.TIKZ_PATH));
		return set;
	}

	/**
	 * Key of the standard library output configuration.
	 */
	public static final String CSP_LIBRARY_OUTPUT = "CSP_LIBRARY_OUTPUT";

	/**
	 * Key of the TikZ output configuration.
	 */
	public static final String TIKZ_OUTPUT = "TIKZ_OUTPUT";

	private OutputConfiguration buildConfig(String name, String descr, String dir) {
		final var result = new OutputConfiguration(name);
		result.setDescription(descr);
		result.setOutputDirectory(dir);
		result.setOverrideExistingResources(true);
		result.setCreateOutputDirectory(true);
		result.setCleanUpDerivedResources(true);
		result.setSetDerivedProperty(true);
		result.setKeepLocalHistory(true);
		return result;
	}

}
