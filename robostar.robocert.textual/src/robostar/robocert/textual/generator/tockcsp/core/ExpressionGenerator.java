/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tockcsp.core;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;

import circus.robocalc.robochart.BinaryExpression;
import circus.robocalc.robochart.BooleanExp;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.FloatExp;
import circus.robocalc.robochart.IntegerExp;
import circus.robocalc.robochart.Neg;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.RoboChartPackage;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator;
import circus.robocalc.robochart.textual.RoboCalcTypeProvider;
import circus.robocalc.robochart.util.RoboChartSwitch;
import robostar.robocert.textual.generator.utils.VariableHelper;

/**
 * The RoboCert expression generator.
 * <p>
 * This implements (some of) the RoboChart expression language, but does so in a
 * way with some differences to how the reference expression compiler works. The
 * main difference is that variables resolve to specification memory slots, and
 * constants to target parameterisation.
 * <p>
 * In the future, RoboCert may target the RoboChart generator directly. It used
 * to in the past, but this proved too complex.
 *
 * @author Matt Windsor
 */
public record ExpressionGenerator(TemporaryVariableGenerator bg, VariableHelper vx, TypeGenerator typeGen,
		RoboCalcTypeProvider typeProvider) {

	@Inject
	public ExpressionGenerator {
		Objects.requireNonNull(bg);
		Objects.requireNonNull(vx);
		Objects.requireNonNull(typeGen);
		Objects.requireNonNull(typeProvider);
	}

	/**
	 * Generates CSP-M for an expression.
	 *
	 * @param it the expression to generate.
	 * @return CSP-M for the expression.
	 */
	public CharSequence generate(Expression it) {
		return new RoboChartSwitch<CharSequence>() {
			@Override
			public CharSequence defaultCase(EObject e) {
				throw new IllegalArgumentException(
						"unsupported expression (only a few are supported so far): %s".formatted(e));
			}

			@Override
			public CharSequence caseBinaryExpression(BinaryExpression b) {
				// TODO(@MattWindsor91): this can likely be optimised for precedence?
				return tryGenerateArithBinary(b).orElseGet(() -> "(%s) %s (%s)".formatted(generate(b.getLeft()),
						generateCspOp(b), generate(b.getRight())));
			}

			@Override
			public CharSequence caseBooleanExp(BooleanExp b) {
				return b.getValue();
			}

			@Override
			public CharSequence caseIntegerExp(IntegerExp i) {
				return Integer.toString(i.getValue());
			}

			@Override
			public CharSequence caseFloatExp(FloatExp f) {
				// See robochart-csp-gen#39.
				throw new IllegalArgumentException(
						"floating point expressions are unsupported for CSP generation: %s".formatted(f));
			}

			@Override
			public CharSequence caseRefExp(RefExp r) {
				if (!(r.getRef() instanceof Variable v)) {
					return null;
				}
				// in RoboCert, variables are either RoboChart constants or spec-level bindings.
				// This is the main difference between our generator and that of RoboCert.
				return switch (v.getModifier()) {
				case CONST -> vx.constantId(v);
				case VAR -> bg.generateExpressionName(v);
				};
			}

			@Override
			public CharSequence caseNeg(Neg m) {
				return "-(" + generate(m.getExp()) + ")";
			}
		}.doSwitch(it);
	}

	/**
	 * Tries to see if this binary expression is an arithmetic operator; if so,
	 * expands it to the checked RoboChart definition.
	 *
	 * @param it the expression to generate.
	 * @return an Optional containing the expression; if the optional is empty, the
	 *         binary expression is either handled at CSP-level or is ill-formed.
	 */
	private Optional<CharSequence> tryGenerateArithBinary(BinaryExpression it) {
		return generateArithOp(it).map(op -> {
			// TODO(@MattWindsor91): type getting from a context
			final var type = typeGen.compileType(typeProvider.typeFor(it));
			return "%s(%s, %s, %s)".formatted(op, generate(it.getLeft()), generate(it.getRight()), type);
		});
	}

	private Optional<String> generateArithOp(BinaryExpression it) {
		final var op = switch (it.eClass().getClassifierID()) {
		case RoboChartPackage.PLUS -> "Plus";
		case RoboChartPackage.MINUS -> "Minus";
		case RoboChartPackage.MULT -> "Mult";
		case RoboChartPackage.DIV -> "Div";
		case RoboChartPackage.MODULUS -> "Modulus";
		default -> null;
		};
		return Optional.ofNullable(op);
	}

	private String generateCspOp(BinaryExpression it) {
		return switch (it.eClass().getClassifierID()) {
		case RoboChartPackage.AND -> "and";
		case RoboChartPackage.OR -> "or";
		case RoboChartPackage.LESS_THAN -> "<";
		case RoboChartPackage.LESS_OR_EQUAL -> "<=";
		case RoboChartPackage.EQUALS -> "==";
		case RoboChartPackage.DIFFERENT -> "!=";
		case RoboChartPackage.GREATER_OR_EQUAL -> ">=";
		case RoboChartPackage.GREATER_THAN -> ">";
		default -> throw new IllegalArgumentException(
				"unsupported binary expression (only a few are supported so far): %s".formatted(it));
		};
	}
}
