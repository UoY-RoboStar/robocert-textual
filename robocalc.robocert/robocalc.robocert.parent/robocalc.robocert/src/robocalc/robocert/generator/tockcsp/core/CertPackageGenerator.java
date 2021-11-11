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
package robocalc.robocert.generator.tockcsp.core;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.ll.CSPGroupGenerator;
import robocalc.robocert.generator.tockcsp.seq.SeqGroupGenerator;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
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
public class CertPackageGenerator {
	@Inject
	private AssertionGroupGenerator ag;
	@Inject
	private CSPGroupGenerator cg;
	@Inject
	private SeqGroupGenerator sg;
	@Inject
	private ImportGenerator ig;
	@Inject
	private UnsupportedSubclassHandler ush;

	/**
	 * @return generated CSP for all elements.
	 *
	 * @param pkg the package being generated.
	 */
	public CharSequence generate(CertPackage pkg) {
		return String.join("\n\n", generateHeader(), ig.generateImports(pkg.eResource()), generateGroups(pkg));
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
		return String.join("\n\n", pkg.getGroups().stream().map(this::generateGroup).toList());
	}

	private CharSequence generateGroup(Group it) {
		// TODO(@MattWindsor91): dependency-inject these somehow
		if (it instanceof CSPGroup c)
			return cg.generate(c);
		if (it instanceof SequenceGroup s)
			return sg.generate(s);
		if (it instanceof AssertionGroup a)
			return ag.generate(a);
		return ush.unsupported(it, "group", "");
	}

}
