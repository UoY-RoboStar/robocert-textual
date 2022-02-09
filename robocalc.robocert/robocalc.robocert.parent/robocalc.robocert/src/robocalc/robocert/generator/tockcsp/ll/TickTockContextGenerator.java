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
package robocalc.robocert.generator.tockcsp.ll;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.core.TargetGroupGenerator;
import robocalc.robocert.generator.utils.UnsupportedSubclassHandler;
import robocalc.robocert.model.robocert.CSPContextSource;
import robocalc.robocert.model.robocert.EventSetCSPFragment;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.ProcessCSPFragment;
import robocalc.robocert.model.robocert.SpecGroup;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetGroupSource;

/**
 * Generates the appropriate tick-tock 'context' (minimal covering set of all
 * events in a process) for use in model-shifting tick-tock refinement to traces
 * refinement.
 *
 * See: J. Baxter, P. Ribeiro, A. Cavalcanti: Sound reasoning in tock-CSP. Acta
 * Informatica. (2021).
 *
 * @author Matt Windsor
 */
public class TickTockContextGenerator {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private UnsupportedSubclassHandler ush;
	@Inject
	private TargetGroupGenerator tg;

	/**
	 * Lifts the given process body into the tick-tock context of p.
	 *
	 * @param s     the source to use for lifting.
	 * @param inner the body to lift.
	 * @return the lifted body.
	 */
	public CharSequence liftTickTock(CSPContextSource s, CharSequence inner) {
		return csp.function(csp.namespaced(generateRef(s), "TT"), inner);
	}

	/**
	 * Generates a reference to a tick-tock context for the given source.
	 *
	 * @param s the source of the tick-tock context.
	 *
	 * @return CSP-M referring to the tick-tock context.
	 */
	public CharSequence generateRef(CSPContextSource s) {
		// Note: the control flow is slightly convoluted here, accounting for
		// the fact that many of these sources are thinly veiled wrappers over
		// each other.
		if (s instanceof ProcessCSPFragment p)
			s = p.getEvents(); // TODO(@MattWindsor91): pick up target here
		if (s instanceof EventSetCSPFragment e)
			return csp.namespaced(e.getName(), TargetField.TICK_TOCK_CONTEXT.toString());

		// These sources are just wrappers over a target group:
		if (s instanceof Interaction z)
			s = z.getGroup();
		if (s instanceof TargetGroupSource t)
			s = t.getTargetGroup();
		// The fallthrough here is intentional:
		if (s instanceof SpecGroup g)
			s = g.getTarget();
		if (s instanceof Target t)
			return tg.getFullCSPName(t, TargetField.TICK_TOCK_CONTEXT);

		return ush.unsupported(s, "CSP context source", "{}");
	}
}