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
package robocalc.robocert.generator.tockcsp.core;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.collect.Streams;
import com.google.inject.Inject;

import circus.robocalc.robochart.BasicPackage;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.generator.utils.CertPackageExtensions;
import robocalc.robocert.generator.utils.FilenameExtensions;
import robocalc.robocert.generator.utils.RCPackageExtensions;
import robocalc.robocert.model.robocert.CertPackage;

/**
 * A generator that expands out imports for a top-level resource.
 *
 * @author Matt Windsor
 */
public class ImportGenerator {
	// TODO(@MattWindsor91): try merge this with upstream. We can't easily
	// just take upstream directly, as it doesn't directly pick up everything
	// that generates an import in robocert. However, this version of the
	// import generator has diverged quite a bit from the original!

	// TODO(@MattWindsor91): we assume there is an instantiations.csp file
	// at the moment, and, as such, a) import it; and b) import every package
	// rather than just the anonymous ones. This should be fixed in line with
	// upstream, eventually, I think?
	@Inject
	private CertPackageExtensions cpx;
	@Inject
	private RCPackageExtensions rpx;
	@Inject
	private FilenameExtensions fx;
	@Inject
	private PathSet ps;
	@Inject
	private CTimedGeneratorUtils gu;

	/**
	 * Generates imports.
	 *
	 * @param r the resource for which we are generating imports.
	 * @return the generated imports.
	 */
	public CharSequence generateImports(Resource r) {
		return imports(r).map("include \"%s\""::formatted).collect(Collectors.joining("\n"));
	}

	// Pulled out of GeneratorUtils
	private Stream<String> imports(Resource r) {
		// Trying to distinct CharSequences doesn't seem to work
		// properly, hence the conversion.
		return Stream.concat(standardImports(), Stream.concat(rcPackageDefImports(r), packageImports(r)))
				.map(CharSequence::toString).distinct();
	}

	/**
	 * Gets the standard RoboChart definitions imports.
	 *
	 * @return an iterator of import filenames.
	 */
	private Stream<CharSequence> standardImports() {
		// robocert_defs is included by this generator, and transitively
		// includes most of the RoboChart prelude.
		return Stream.of(ps.LIBRARY_FROM_PACKAGE_PATH + "/robocert_defs.csp",
				ps.ROBOCHART_FROM_PACKAGE_PATH + "/instantiations.csp");
	}

	/**
	 * Gets 'defs' imports for any RoboChart package reachable through this
	 * resource's set.
	 *
	 * @param r the resource to query.
	 *
	 * @return an iterator of import filenames.
	 */
	private Stream<CharSequence> rcPackageDefImports(Resource r) {
		// TODO(@MattWindsor91): this may sometimes need to be filtered to
		// anonymous only.
		return Streams.stream(rpx.getPackages(r)).map(fx::getCSPDefsFileName);
	}

	/**
	 * Scrapes the resource's package for imports.
	 *
	 * The exact type of imports retrieved in this way depends on the type of
	 * package.
	 *
	 * @param it the resource to query.
	 *
	 * @return a stream of input filenames.
	 */
	private Stream<CharSequence> packageImports(Resource r) {
		return Streams.stream(rpx.getBasicPackage(r)).flatMap(this::packageImports);
	}

	private Stream<CharSequence> packageImports(BasicPackage p) {
		return Stream.concat(namedImports(p), packageImportsInner(p));
	}

	private Stream<CharSequence> packageImportsInner(BasicPackage p) {
		// TODO(@MattWindsor91): is this ever not a CertPackage in practice?
		if (p instanceof CertPackage c)
			return cpx.getReferencedElements(c).flatMap(this::elementImports);
		return Stream.of(fx.getCSPMainFileName(p));
	}

	private Stream<CharSequence> elementImports(NamedElement element) {
		return Streams.stream(rpx.getPackage(element)).flatMap(p -> Stream
				.concat(Stream.of(fx.getCSPDefsFileName(p), fx.getCSPTopModuleFileName(element)), namedImports(p)));
	}

	private Stream<CharSequence> namedImports(BasicPackage p) {
		return gu.allImports(p).stream().map(fx::getCSPDefsFileName);
	}
}
