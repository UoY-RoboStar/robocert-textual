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

import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.intf.core.TargetField;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.SpecGroup;
import robocalc.robocert.model.robocert.Target;

/**
 * Generator for sequence groups.
 *
 * This generator handles the top-level, unparametrised parts of a sequence
 * group.
 *
 * @see SeqGroupParametricGenerator
 */
abstract public class SpecGroupGenerator<T extends SpecGroup> extends GroupGenerator<T> {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private TargetBodyGenerator tg;
	@Inject
	private TargetGenerator tgg;

	/**
	 * Generates the body elements of the open definition.
	 *
	 * @param it the specification group in question.
	 *
	 * @return CSP-M for the open definition body.
	 */
	protected abstract Stream<CharSequence> openDefBodyElements(T it);

	@Override
	protected Stream<CharSequence> generateBodyElements(T group) {
		return Stream.of(openDef(group), closedDef(group));
	}

	@Override
	protected boolean isTimed(T group) {
		// Only *some* of a spec group's elements need to be timed.
		return false;
	}

	@Override
	protected boolean isInModule(T group) {
		// Spec groups always generate a module.
		// TODO(@MattWindsor91): do they?
		return true;
	}

	/**
	 * Generates a process definition for the 'closed' form of this group.
	 *
	 * The closed form has no parameters, with all constants assigned values either
	 * from its target's instantiation or from the top-level instantiations.csp
	 * file.
	 *
	 * @param it the group for which we are generating a closed form.
	 *
	 * @return CSP defining the 'closed' form of this group.
	 */
	private CharSequence closedDef(T it) {
		return csp.instance(SpecGroupField.PARAMETRIC_CLOSED.toString(), openSig(it, it.getInstantiation()));
	}

	/**
	 * Generates an external reference for the 'open' form of this group.
	 *
	 * The open form has parameters exposed, and any reference to it must fill those
	 * parameters using either values in the given instantiation or, where values
	 * are missing, references to the instantiations CSP file.
	 *
	 * @param it the group for which we are generating CSP.
	 *
	 * @return generated CSP for referring to the 'open' form of this group.
	 */
	public CharSequence generateOpenRef(T it, Instantiation instantiation) {
		var name = csp.namespaced(it.getName(), SpecGroupField.PARAMETRIC_OPEN.toString());
		return csp.function(name, openSigParams(it, instantiation));
	}

	/**
	 * Generates a process definition for the 'open' form of this target.
	 *
	 * @param it the group for which we are generating an open form.
	 *
	 * @return generated CSP for the 'open' form of a spec group.
	 */
	private CharSequence openDef(T it) {
		return csp.module(openSig(it, null), openDefBody(it));
	}

	/**
	 * Generates the body of this specification's 'open' form.
	 *
	 * @implNote This merges elements from two sources: a set common to all
	 *           specifications (such as the target definition), and a set
	 *           overridden by the downstream class.
	 *
	 * @param it the group for which we are generating an open form.
	 *
	 * @return CSP-M for the body of the 'open' form of the spec group.
	 */
	private CharSequence openDefBody(T it) {
		return csp.innerJoin(Stream.concat(commonOpenDefBodyElements(it), openDefBodyElements(it)));
	}

	/**
	 * Generates the set of elements common to all 'open' form bodies.
	 *
	 * @implNote This is currently the target definition, but may expand.
	 *
	 * @param it the group for which we are generating an open form.
	 *
	 * @return CSP-M for common elements of the body of the 'open' form of the spec
	 *         group.
	 */
	private Stream<CharSequence> commonOpenDefBodyElements(T it) {
		// Space here for expansion.
		return Stream.of(targetDef(it));
	}

	private CharSequence targetDef(T it) {
		// NOTE(@MattWindsor91): as far as I know, this needn't be timed
		return csp.definition(SpecGroupParametricField.TARGET.toString(),
				targetDefBody(it.getTarget(), it.getInstantiation()));
	}

	private CharSequence targetDefBody(Target t, Instantiation inst) {
		// We're accessing the open form of the target here, filling in
		// the group's own instantiation.
		var name = tgg.getFullCSPName(t, TargetField.OPEN);
		return csp.function(name, tg.generateRefParams(t, null, inst, true));
	}

	/**
	 * Generates the signature of an open sequence group definition or reference.
	 *
	 * Because the parameters used in the definition are just the constant IDs,
	 * which are also how we refer to any fallback references to the instantiations
	 * file, both definitions and references can have the same signature generator.
	 *
	 * @param it        the group for which we are generating an open form.
	 * @param outerInst any instantiation being applied at the outer level (may be
	 *                  null).
	 *
	 * @return CSP referring to, or giving the signature of, the 'open' form of this
	 *         group.
	 */
	private CharSequence openSig(T it, Instantiation outerInst) {
		return csp.function(SpecGroupField.PARAMETRIC_OPEN.toString(), openSigParams(it, outerInst));
	}

	private CharSequence[] openSigParams(T it, Instantiation outerInst) {
		return tg.generateRefParams(it.getTarget(), it.getInstantiation(), outerInst, false);
	}

}
