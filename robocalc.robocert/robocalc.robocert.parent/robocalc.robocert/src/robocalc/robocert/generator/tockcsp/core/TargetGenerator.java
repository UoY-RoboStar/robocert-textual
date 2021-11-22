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

import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.generator.utils.TargetParameterResolver;
import robocalc.robocert.generator.utils.VariableExtensions;
import robocalc.robocert.model.robocert.CertExpr;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.Target;

/**
 * Generates CSP-M for the various forms of a target.
 *
 * @author Matt Windsor
 */
public class TargetGenerator {
	@Inject
	private ExpressionGenerator eg;
	@Inject
	private TargetParameterResolver pr;
	@Inject
	private VariableExtensions vx;
	@Inject
	private CTimedGeneratorUtils gu;

	/**
	 * Generates the RHS of an open target definition.
	 *
	 * @implNote This is currently just a reference to the corresponding RoboStar
	 *           model element, but this might change if we add any targets that
	 *           don't correspond to such things. There is currently no way to
	 *           specify an optimised definition.
	 *
	 * @param it the target.
	 *
	 * @return CSP-M for the target definition.
	 */
	public CharSequence generateDef(Target it) {
		/*
		 * In email with Pedro (4 Aug): the target of a refinement against a (simple)
		 * specification should usually be unoptimised (D__); model comparisons should
		 * usually be optimised (O__).
		 *
		 * TODO(@MattWindsor91): eventually, we should be able to select the
		 * optimisation level.
		 */
		return gu.getFullProcessName(it.getElement(), false);
	}

	/**
	 * Generates the parameter list of a target reference.
	 *
	 * @param t    the target.
	 * @param lastInst any instantiation that has already been applied to the target.
	 * @param thisInst the instantiation being applied to the target here.
	 * @param withId whether to include the ID parameter.
	 *
	 * @return an array of CSP-M elements corresponding to the arguments of a target
	 *         that are not yet instantiated, instantiated with the given instantiation.
	 */
	public CharSequence[] generateRefParams(Target t, Instantiation lastInst, Instantiation thisInst, boolean withId) {
		// TODO(@MattWindsor91): work out what we need here to have derived
		// groups.  Maybe a stack of instantiations?
		var params = pr.excludeInstantiated(pr.parameterisation(t), lastInst).map(k -> generateConstant(thisInst, k));
		if (withId)
			params = Stream.concat(Stream.of(ID), params);		
		return params.toArray(CharSequence[]::new);
	}

	private static final String ID = "{- id -} 0";

	// TODO(@MattWindsor91): move this?

	/**
	 * Generates the value of a constant given an instantiation.
	 *
	 * If the value isn't available, we emit the constant ID; this will resolve
	 * either to a parameter (when defining an open target) or a definition in
	 * instantiations.csp (when defining a closed target).
	 *
	 * If the value is available, we emit a CSP comment giving the name, for
	 * clarity.
	 *
	 * @param it the instantiation (may be null).
	 * @param k  the constant whose value is requested.
	 *
	 * @return a CSP string expanding to the value of the constant.
	 */
	private CharSequence generateConstant(Instantiation it, Variable k) {
		var id = vx.constantId(k);
		var instantiation = getConstant(it, k);
		return instantiation == null ? id : generateNamedExpression(instantiation, id);
	}

	private CertExpr getConstant(Instantiation it, Variable k) {
		return it == null ? null : it.getConstant(k);
	}

	private CharSequence generateNamedExpression(CertExpr it, CharSequence id) {
		return "{- %s -} %s".formatted(id, eg.generate(it));
	}
}
