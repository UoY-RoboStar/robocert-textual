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

import circus.robocalc.robochart.BasicPackage;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.resource.Resource;
import robocalc.robocert.generator.utils.PackageFinder;
import robocalc.robocert.model.robocert.CertPackage;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetGroup;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * A generator that expands out imports for a top-level resource.
 *
 * @author Matt Windsor
 */
public record ImportGenerator(PathSet ps,
                              PackageFinder pf,
                              CTimedGeneratorUtils gu) {
  // TODO(@MattWindsor91): try merge this with upstream. We can't easily
  // just take upstream directly, as it doesn't directly pick up everything
  // that generates an import in robocert. However, this version of the
  // import generator has diverged quite a bit from the original!

  // TODO(@MattWindsor91): we assume there is an instantiations.csp file
  // at the moment, and, as such, a) import it; and b) import every package
  // rather than just the anonymous ones. This should be fixed in line with
  // upstream, eventually, I think?

  @Inject
  public ImportGenerator {
  }

  /**
   * Generates imports.
   *
   * @param r the resource for which we are generating imports.
   * @return the generated imports.
   */
  public CharSequence generate(Resource r) {
    return imports(r).map("include \"%s\""::formatted).collect(Collectors.joining("\n"));
  }

  // Pulled out of GeneratorUtils
  private Stream<String> imports(Resource r) {
    // We need to import:
    // - the RoboCert standard library (transitively, the RoboChart standard library);
    // - for each anonymous RoboChart package in the resource set, its resource defs file;
    // - for each RoboCert package in the resource:
    //   - for each Target, its associated RoboChart element's:
    //     - resource defs file;
    //     - top-level module file;
    //     - imports' defs files;
    //   - for each import, its resource defs file.
    return Streams.concat(standardImports(), anonymousImports(r), certPackageImports(r)).distinct();
  }

  private Stream<String> standardImports() {
    // robocert_defs is included by this generator, and transitively
    // includes most of the RoboChart prelude.
    return Stream.of(ps.LIBRARY_FROM_PACKAGE_PATH + "/robocert_defs.csp",
        ps.ROBOCHART_FROM_PACKAGE_PATH + "/instantiations.csp"
        );
  }

  private Stream<String> anonymousImports(Resource r) {
    return pf.packagesInSiblingResources(r, RCPackage.class).filter(this::isAnonymousRc)
        .map(x -> defsInclude(x.eResource()));
  }

  private boolean isAnonymousRc(RCPackage p) {
    return p.getName() == null && Objects.equals(p.eResource().getURI().fileExtension(), "rct");
  }

  private Stream<String> certPackageImports(Resource r) {
    return pf.packagesInResource(r, CertPackage.class)
        .flatMap(p -> Stream.concat(targetImports(p), namedImports(p)));
  }

  private Stream<String> targetImports(CertPackage p) {
    return targetElements(p).flatMap(this::elementImports);
  }

  private Stream<NamedElement> targetElements(CertPackage p) {
    // We used to pick up only targets referenced in a SpecGroup here for efficiency, but
    // this means that the definitions of the targets themselves break - for perhaps obvious
    // reasons.  If we want to optimise out unused targets, we'll need to optimise out their
    // definitions entirely (and then make sure they can't be used by other files?).
    return StreamHelpers.filter(p.getGroups().stream(), TargetGroup.class)
        .flatMap(x -> x.getTargets().stream()).map(
            Target::getElement);
  }

  private Stream<String> elementImports(NamedElement elt) {
    // This is basically the same as the analogous code in GeneratorUtils
    final var resource = elt.eResource();
    return rcPackages(resource).flatMap(p -> elementImportsInPackage(resource, elt, p));
  }

  private Stream<String> elementImportsInPackage(Resource r, NamedElement elt, RCPackage p) {
    final var directs = Stream.of(
        defsInclude(r),
        ps.include(gu.calculateTopModule(elt))
    );
    return Stream.concat(directs, namedImports(p));
  }

  private String defsInclude(Resource r) {
    return ps.include(gu.getFileName(r) + "_defs");
  }

  private Stream<RCPackage> rcPackages(Resource r) {
    return pf.packagesInResource(r, RCPackage.class);
  }

  private Stream<String> namedImports(BasicPackage p) {
    return gu.allImports(p).stream().map(x -> defsInclude(x.eResource()));
  }
}
