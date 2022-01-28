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

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.intf.seq.SubsequenceGenerator;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.model.robocert.Branch;
import robocalc.robocert.model.robocert.ElseGuard;
import robocalc.robocert.model.robocert.EmptyGuard;
import robocalc.robocert.model.robocert.ExprGuard;
import robocalc.robocert.model.robocert.Guard;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Generates branches and guards.
 *
 * @author Matt Windsor
 */
public record BranchGenerator(ExpressionGenerator eg,
															SubsequenceGenerator sg) {

	/**
	 * Constructs a branch generator.
	 *
	 * @param eg an expression generator.
	 * @param sg a subsequence generator.
	 */
	@Inject
	public BranchGenerator {
	}

	/**
	 * Generates CSP-M for a branch.
	 *
	 * @param b   branch for which we are generating CSP-M.
	 * @param ctx context of the lifeline for which we are generating CSP-M.
	 * @return the generated CSP-M.
	 */
	public CharSequence generate(Branch b, LifelineContext ctx) {
		// No whitespace because the empty guard should be a no-op on the body.
		return String.join("", guard(b.getGuard()), sg.generate(b.getBody(), ctx));
	}

	private CharSequence guard(Guard g) {
		if (g instanceof EmptyGuard) {
			return "";
		}
		if (g instanceof ExprGuard e) {
			return "%s & ".formatted(eg.generate(e.getExpr()));
		}
		if (g instanceof ElseGuard l) {
			return "{- else -} not %s & ".formatted(elseGuard(l));
		}
		throw new IllegalArgumentException("unsupported guard type: %s".formatted(g));
	}

	private CharSequence elseGuard(ElseGuard l) {
		return neighbourExprGuards(l).map(ExprGuard::getExpr).map(eg::generate)
				.collect(Collectors.joining(" and ", "(", ")"));
	}

	private Stream<ExprGuard> neighbourExprGuards(ElseGuard l) {
		return StreamHelpers.filter(neighbourGuards(l), ExprGuard.class);
	}

	private Stream<Guard> neighbourGuards(ElseGuard l) {
		return l.getParent().getFragment().getBranches().stream().map(Branch::getGuard);
	}
}
