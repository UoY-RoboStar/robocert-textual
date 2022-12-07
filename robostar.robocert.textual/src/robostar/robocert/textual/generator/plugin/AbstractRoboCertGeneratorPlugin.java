/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.plugin;

import com.google.common.collect.Iterators;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.CertPackage;
import robostar.robocert.textual.generator.utils.PackageGenerator;

/**
 * Abstractly implements the RoboCert style of generator.
 * <p>
 * <p>
 * This involves copying over a standard library of (CSP, TikZ, etc) definitions, then generating
 * separately for each package.
 *
 * @author Matt Windsor
 */
public abstract class AbstractRoboCertGeneratorPlugin extends AbstractGenerator implements RoboCertGeneratorPlugin {

    @Override
    public void doGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
        generateLibrary(fsa, context);
        generatePackages(input, fsa, context);
    }

    private void generateLibrary(IFileSystemAccess2 fsa, IGeneratorContext context) {
        libGenerator().generate(getClass(), fsa, context);
    }

    private void generatePackages(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
        final var pkgGen = pkgGenerator();
        final var contents = EcoreUtil.getAllContents(input, true);
        final var packages = Iterators.filter(contents, CertPackage.class);

        while (packages.hasNext()) {
            if (context.getCancelIndicator().isCanceled()) {
                break;
            }
            pkgGen.generate(fsa, context, packages.next());
        }
    }


    /**
     * Factory method for the package generator.
     *
     * @return a generator mapping RoboCert packages to output.
     */
    protected abstract PackageGenerator pkgGenerator();

    /**
     * Factory method for the library generator.
     *
     * @return a library generator, which should be configured in-line here.
     */
    protected abstract StandardLibraryGenerator libGenerator();

}
