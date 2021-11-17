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
package robocalc.robocert.generator.tockcsp.seq;

import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.google.inject.Inject;

import circus.robocalc.robochart.Variable;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.tockcsp.core.GroupGenerator;
import robocalc.robocert.generator.tockcsp.core.TargetGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.utils.TargetExtensions;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Generator for sequence groups.
 *
 * This generator handles the top-level, unparametrised parts of a sequence
 * group.
 *
 * @see SeqGroupParametricGenerator
 */
public class SequenceGroupGenerator extends GroupGenerator<SequenceGroup> {
	// TODO(@MattWindsor91): generalise this to SpecGroups

	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private TargetGenerator tg;
	@Inject
	private TargetExtensions tx;
	@Inject
	private MessageSetGenerator msg;
	@Inject
	private SeqGroupParametricGenerator pg;

	@Override
	protected Stream<CharSequence> generateBodyElements(SequenceGroup group) {
		return Stream.of(openDef(group), closedDef(group));
	}

	@Override
	protected Stream<CharSequence> generatePrivateElements(SequenceGroup group) {
		return Stream.of(msg.generateNamedSets(group.getMessageSets(), group.getTarget()));
	}

	@Override
	protected CharSequence typeName(SequenceGroup group) {
		return "SEQUENCE";
	}

	@Override
	protected boolean isTimed(SequenceGroup group) {
		// Sequence groups are never timed, as only some of their elements need
		// to be timed.
		return false;
	}

	@Override
	protected boolean isInModule(SequenceGroup group) {
		// Sequence groups always generate a module.
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
	private CharSequence closedDef(SequenceGroup it) {
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
	public CharSequence generateOpenRef(SequenceGroup it, Instantiation instantiation) {
		var name = csp.namespaced(it.getName(), SpecGroupField.PARAMETRIC_OPEN.toString());
		return csp.function(name, openSigParams(it, instantiation));
	}

	/**
	 * Generates a process definition for the 'open' form of this target.
	 *
	 * @param it            the group for which we are generating an open form.
	 * @param instantiation the instantiation (may be null).
	 *
	 * @return generated CSP for the 'open' form of a sequence's target.
	 */
	public CharSequence openDef(SequenceGroup it) {
		return csp.module(openSig(it, null), pg.generateParametric(it));
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
	private CharSequence openSig(SequenceGroup it, Instantiation outerInst) {
		return csp.function(SpecGroupField.PARAMETRIC_OPEN.toString(), openSigParams(it, outerInst));
	}

	private CharSequence[] openSigParams(SequenceGroup it, Instantiation outerInst) {
		var stream = uninstantiatedConstants(it).map(x -> tg.generateConstant(outerInst, x));
		return stream.toArray(CharSequence[]::new);
	}

	private Stream<Variable> uninstantiatedConstants(SequenceGroup it) {
		return Streams.stream(tx.uninstantiatedConstants(it.getTarget(), it.getInstantiation()));
	}
}
