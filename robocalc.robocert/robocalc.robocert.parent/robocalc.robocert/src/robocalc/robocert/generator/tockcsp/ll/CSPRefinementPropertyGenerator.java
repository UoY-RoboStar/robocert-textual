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

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;

import robocalc.robocert.model.robocert.CSPModel;
import robocalc.robocert.model.robocert.CSPRefinementOperator;
import robocalc.robocert.model.robocert.CSPRefinementProperty;

/**
 * Generates CSP refinement assertions.
 *
 * @author Matt Windsor
 */
public class CSPRefinementPropertyGenerator {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private CSPProcessSourceGenerator psg;

	/**
	 * Generates CSP-M for a refinement property.
	 *
	 * This is usually just a straightforward expansion of the refinement, unless
	 * the property is an equality, in which case we expand it to two refinements
	 * (one in each direction).
	 *
	 * @param it the refinement property in question.
	 *
	 * @return CSP-M for the given property.
	 */
	public CharSequence generateProperty(CSPRefinementProperty it) {
		var refine = generateRefinement(it);
		if (it.getType() == CSPRefinementOperator.EQUALS) {
			return String.join("\n", refine, generateRefinement(invert(it)));
		}
		return refine;
	}

	/**
	 * Inverts a CSP refinement property.
	 *
	 * @param p the property to invert.
	 *
	 * @return an inverted copy of the given property.
	 */
	private CSPRefinementProperty invert(CSPRefinementProperty p) {
		var inv = EcoreUtil.copy(p);
		inv.setRhs(p.getLhs());
		inv.setLhs(p.getRhs());
		return inv;
	}

	/**
	 * Generates a refinement for a refinement property.
	 *
	 * Note that 'lhs' and 'rhs' on the property get flipped during generation, as
	 * CSP-M's refinement operator is 'is-refined-by', not 'refined'.
	 *
	 * @param p the property for which we are generating a property.
	 *
	 * @return CSP-M for the generated refinement.
	 */
	private CharSequence generateRefinement(CSPRefinementProperty p) {
		// Currently, both models supported by RoboCert become the trace model
		// at the FDR level (through model shifting), hence the hardcoded "T".
		var mdl = p.getModel();
		return csp.assertion(p.isNegated(),
				liftTauPriority(mdl, csp.refine(psg.generate(p.getRhs(), mdl), psg.generate(p.getLhs(), mdl), "T")));
	}

	/**
	 * @return the appropriate FDR tau priority pragma for this model.
	 */
	private CharSequence liftTauPriority(CSPModel m, CharSequence inner) {
		return m == CSPModel.TICK_TOCK ? inner : csp.tauPrioritiseTock(inner);
	}
}
