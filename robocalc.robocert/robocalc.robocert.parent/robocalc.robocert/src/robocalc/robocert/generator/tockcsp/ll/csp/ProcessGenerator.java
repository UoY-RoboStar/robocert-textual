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
package robocalc.robocert.generator.tockcsp.ll.csp;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.core.SpecGroupElementFinder;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.generator.tockcsp.ll.TickTockContextGenerator;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
import robocalc.robocert.model.robocert.CSPModel;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.Process;
import robocalc.robocert.model.robocert.ProcessCSPFragment;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetGroupSource;

/**
 * Generates CSP-M that interprets {@link Process}es as references to the
 * relevant CSP processes.
 *
 * @author Matt Windsor
 */
public class ProcessGenerator {
	@Inject
	private TargetGenerator tg;
	@Inject
	private TickTockContextGenerator tt;
	@Inject
	private SpecGroupElementFinder sl;
	@Inject
	private UnsupportedSubclassHandler ush;

	/**
	 * Generates CSP-M referencing a process, potentially lifted into
	 * its tick-tock context.
	 *
	 * @param p the process to generate.
	 * @param m the target semantic model.
	 *
	 * @return CSP-M for the generated process.
	 */
	public CharSequence generate(Process p, CSPModel m) {
		final var inner = generateInner(p);
		return m == CSPModel.TICK_TOCK ? tt.liftTickTock(p, inner) : inner;
	}

	private CharSequence generateInner(Process p) {
		// TODO(@MattWindsor91): pattern matched switch, eventually

		if (p instanceof ProcessCSPFragment c)
			return c.getName();
		if (p instanceof Interaction s)
			return sl.getFullCSPName(s);
		if (p instanceof TargetGroupSource g)
			return sl.getFullCSPName(g.getTargetGroup(), SpecGroupParametricField.TARGET);
		if (p instanceof Target t)
			return tg.getFullCSPName(t, TargetField.CLOSED);

		return ush.unsupported(p, "CSP process source", "STOP");
	}
}
