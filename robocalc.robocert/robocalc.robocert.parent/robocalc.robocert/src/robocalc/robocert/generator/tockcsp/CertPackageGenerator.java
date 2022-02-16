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
package robocalc.robocert.generator.tockcsp;

import com.google.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;
import robocalc.robocert.generator.tockcsp.core.AssertionGroupGenerator;
import robocalc.robocert.generator.tockcsp.core.ImportGenerator;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPGroupGenerator;
import robocalc.robocert.generator.tockcsp.seq.SequenceGroupGenerator;
import robocalc.robocert.model.robocert.AssertionGroup;
import robocalc.robocert.model.robocert.CSPGroup;
import robocalc.robocert.model.robocert.CertPackage;
import robocalc.robocert.model.robocert.Group;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Generates CSP-M for {@link CertPackage}s.
 *
 * @author Matt Windsor
 */
public record CertPackageGenerator (
	AssertionGroupGenerator ag,
	CSPGroupGenerator cg,
	SequenceGroupGenerator sg,
	TargetGenerator tg,
	ImportGenerator ig
) {

	/**
	 * Constructs a CertPackage generator.
	 * @param ag the assertion group generator.
	 * @param cg the CSP group generator.
	 * @param sg the sequence group generator.
	 * @param tg the target generator.
	 * @param ig the import generator.
	 */
	@Inject
	public CertPackageGenerator {
		Objects.requireNonNull(ag);
		Objects.requireNonNull(cg);
		Objects.requireNonNull(sg);
		Objects.requireNonNull(tg);
		Objects.requireNonNull(ig);
	}

	/**
	 * @return generated CSP for all elements.
	 *
	 * @param pkg the package being generated.
	 */
	public CharSequence generate(CertPackage pkg) {
		final var header = generateHeader();
		final var imports = ig.generate(pkg.eResource());
		final var targets = tg.generate(pkg.getTargets());
		final var groups = generateGroups(pkg);
		return String.join("\n\n", header, imports, targets, groups);
	}

	/**
	 * Similar to CUntimedGenerator in RoboChart, and should probably include the
	 * version eventually too.
	 *
	 * @return the generated header.
	 */
	private CharSequence generateHeader() {
		return """
				--- generated by RoboCert
				--- on %s""".formatted(timestamp());
	}

	/**
	 * @return the current datetime as a human-readable timestamp.
	 */
	private String timestamp() {
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
	}

	/**
	 * @return included CSP for all groups.
	 *
	 * @param pkg the top-level package.
	 */
	private String generateGroups(CertPackage pkg) {
		return pkg.getGroups().stream().map(this::generateGroup).collect(Collectors.joining("\n\n"));
	}

	private CharSequence generateGroup(Group it) {
		// TODO(@MattWindsor91): dependency-inject these somehow
		if (it instanceof AssertionGroup a)
			return ag.generate(a);
		if (it instanceof CSPGroup c)
			return cg.generate(c);
		if (it instanceof SequenceGroup s)
			return sg.generate(s);

		throw new IllegalArgumentException("unsupported group: %s".formatted(it));
	}

}
