/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.generator.OutputConfiguration;

import com.google.inject.Inject;

import robostar.robocert.textual.generator.tikz.TikzPathSet;
import robostar.robocert.textual.generator.tockcsp.core.TockCspPathSet;

/**
 * Overrides output configuration to save CSP to csp-gen.
 */
public class RoboCertOutputConfigurationProvider implements IOutputConfigurationProvider {

	@Inject
	private TockCspPathSet tockCspPathSet;


	@Inject
	private TikzPathSet tikzPathSet;

	@Override
	public Set<OutputConfiguration> getOutputConfigurations() {
		// TODO: is this the right thing to do?
		// TODO: PRISM gen

		final var set = new HashSet<OutputConfiguration>(4);

		set.add(buildConfig(IFileSystemAccess.DEFAULT_OUTPUT, "tock-CSP folder", tockCspPathSet.PACKAGE_PATH));
		set.add(buildConfig(CSP_LIBRARY_OUTPUT, "tock-CSP standard library", tockCspPathSet.LIBRARY_PATH));

		set.add(buildConfig(TIKZ_OUTPUT, "TikZ folder", tikzPathSet.DIAGRAM_PATH));
		set.add(buildConfig(TIKZ_LIBRARY_OUTPUT, "TikZ standard library", tikzPathSet.LIBRARY_PATH));
		return set;
	}

	/**
	 * Key of the CSP standard library output configuration.
	 */
	public static final String CSP_LIBRARY_OUTPUT = "CSP_LIBRARY_OUTPUT";

	/**
	 * Key of the TikZ output configuration.
	 */
	public static final String TIKZ_OUTPUT = "TIKZ_OUTPUT";

	/**
	 * Key of the TikZ standard library output configuration.
	 */
	public static final String TIKZ_LIBRARY_OUTPUT = "TIKZ_LIBRARY_OUTPUT";

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
