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

import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.xtext.generator.OutputConfiguration;
import org.eclipse.core.runtime.Platform;

import org.eclipse.xtext.generator.OutputConfigurationProvider;


/**
 * Performs output configuration for the RoboCert generators.
 *
 * <p>Since v0.2 this has been a straightforward port of the RoboChart analogue.
 *
 * @author Alvaro Miyazawa
 * @author Matt Windsor
 */
public class RoboCertOutputConfigurationProvider extends OutputConfigurationProvider {
    @Override
    public Set<OutputConfiguration> getOutputConfigurations() {
        final var config = Platform.getExtensionRegistry().getConfigurationElementsFor(RoboCertGenerator.GEN_ID);
        final var ocp = super.getOutputConfigurations();
        for (var e : config) {
            final var folder = Objects.requireNonNullElse(e.getAttribute("folder"), "src-gen");

            final Object o;
            try {
                o = e.createExecutableExtension("class");
            } catch (CoreException ex) {
                System.err.println("Couldn't instantiate generator plugin " + e.getName());
                ex.printStackTrace();
                continue;
            }

            if (o instanceof AbstractRoboCertGeneratorPlugin g) {
                ocp.add(buildConfig(g, folder));
            }
        }
        return ocp;
    }

    private OutputConfiguration buildConfig(AbstractRoboCertGeneratorPlugin plugin, String dir) {
        final var result = new OutputConfiguration(plugin.ID());
        result.setDescription(plugin.description());
        result.setOutputDirectory("./" + dir);
        result.setOverrideExistingResources(true);
        result.setCreateOutputDirectory(true);
        result.setCleanUpDerivedResources(true);
        result.setSetDerivedProperty(true);
        result.setKeepLocalHistory(true);
        return result;
    }

}
