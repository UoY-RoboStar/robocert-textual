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

import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.utils.name.GroupNamer;
import robocalc.robocert.model.robocert.Target;
import robocalc.robocert.model.robocert.TargetGroup;

/**
 * Generates CSP-M for {@link TargetGroup}s.
 *
 * @author Matt Windsor
 */
public class TargetGroupGenerator extends GroupGenerator<TargetGroup> {
	// TODO(@MattWindsor91): split reference resolution out of this.
	
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private TargetGenerator tg;
	@Inject
	private GroupNamer gn;

	@Override
	protected Stream<CharSequence> generateBodyElements(TargetGroup group) {
		return group.getTargets().stream().map(this::targetDefinition);
	}

	@Override
	protected boolean isTimed(TargetGroup group) {
		// No point in making any part of targets timed.
		return false;
	}
	
	@Override
	protected boolean isInModule(TargetGroup group) {
		// This mainly just makes scoping easier.
		return true;
	}
	
	@Override
	protected CharSequence typeName(TargetGroup group) {
		return "TARGET";
	}

	// TODO(@MattWindsor91): separate this and TargetGroupGenerator.

	private CharSequence targetDefinition(Target t) {
		return csp.module(t.getName(), moduleBody(t));
	}

	private CharSequence moduleBody(Target t) {
		// TODO(@MattWindsor91): the open/closed def could do with being
		// deduplicated with the work in SpecGroupGenerator.
		return String.join("\n", tickTockContext(t), universe(t), openDef(t), closedDef(t));
	}

	private CharSequence tickTockContext(Target t) {
		return csp.instance(TargetField.TICK_TOCK_CONTEXT.toString(),
				csp.function("model_shifting", semEvents(t)));
	}

	private CharSequence universe(Target t) {
		// TODO(@MattWindsor91): GitHub #76: this allows events in more
		// directions than should necessarily be allowed.
		return csp.definition(TargetField.UNIVERSE.toString(), semEvents(t));
	}

	/**
	 * Produces an 'open' reference to the target's definition.
	 * 
	 * This is used by specification groups.
	 *
	 * @param t the target in question.
	 *
	 * @return CSP-M referencing the name of the RoboStar module that defines the
	 *         target.
	 */
	private CharSequence openDef(Target t) {
		// TODO(@MattWindsor91): the argument here should be configurable.
		return csp.definition(TargetField.OPEN.toString(), tg.generateDef(t));
	}

	/**
	 * Produces a closed definition for the target.
	 * 
	 * This is used by things wanting a CSP process.
	 *
	 * @param t the target in question.
	 *
	 * @return CSP-M referencing the name of the RoboStar module that defines the
	 *         target.
	 */
	private CharSequence closedDef(Target t) {
		// TODO(@MattWindsor91): the argument here should be configurable.
		// TODO(@MattWindsor91): should these targets have an initial instantiation?
		var def = csp.function(TargetField.OPEN.toString(), tg.generateRefParams(t, null, null, true));
		return csp.definition(TargetField.CLOSED.toString(), def);
	}
	
	private CharSequence semEvents(Target t) {
		return csp.namespaced(t.getElement().getName(), "sem__events");
	}

	//
	// References
	//

	/**
	 * Gets the full namespaced name of the given field of the given target.
	 *
	 * @param t the target whose field is to be referenced.
	 * @param f the field to reference.
	 *
	 * @return the name of the field from the perspective of code outside the target
	 *         group.
	 */
	public CharSequence getFullCSPName(Target t, TargetField f) {
		return csp.namespaced(gn.getOrSynthesiseName(t.getGroup()), t.getName(), f.toString());
	}
}
