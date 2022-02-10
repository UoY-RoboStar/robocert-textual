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
package robocalc.robocert.generator.tockcsp.memory;

import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableList;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Interaction;

/**
 * Generates memory modules for sequences.
 * 
 * The memory definition we use for RoboCert is fairly simplistic, but similar
 * to that used in other RoboStar languages.  For each sequence that needs a
 * memory, we generate a process that offers each binding in the sequence as an
 * in/out channel.  The process is a recursion that continuously offers the
 * current values of each binding through the out channel, while also offering
 * to accept new values (updating its recursion accordingly).
 * 
 * There is no true concurrency in sequence diagrams, so this should be
 * sufficient.
 *
 * @author Matt Windsor
 */
public record ModuleGenerator(CTimedGeneratorUtils gu, CSPStructureGenerator csp, TypeGenerator typeGen) {
	private static final String MEM_OP_TYPE = "MemOp"; // in robocert_defs
	private static final String LIFT_PROCESS = "lift";
	private static final String RUN_PROCESS = "proc";
	private static final String SYNC_SET = "sync";

	@Inject
	public ModuleGenerator {
		Objects.requireNonNull(gu);
		Objects.requireNonNull(csp);
		Objects.requireNonNull(typeGen);
	}

	/**
	 * Generates the module for a memory.
	 *
	 * @param mem  the memory to generate.
	 *
	 * @return  a CSP-M module containing the memory definition (channels,
	 *          process, and so on).
	 */
	public CharSequence generate(VariableList mem) {
		return csp.moduleWithPrivate(name(mem), generatePrivateBody(mem), generatePublicBody(mem));
	}

	private String name(VariableList it) {
		final var parent = EcoreUtil2.getContainerOfType(it, Interaction.class);
		if (parent == null) {
			return "MISSING_INTERACTION";
		}
		return Objects.requireNonNullElse(parent.getName(), "MISSING_NAME");
	}

	/**
	 * Lifts the CSP-M for a sequence process into a memory context.
	 *
	 * @param seq      the sequence being lifted.
	 * @param process  its pre-generated process.
	 *
	 * @return  process, lifted into seq's memory context.
	 */
	public CharSequence lift(Interaction seq, CharSequence process) {
		return csp.function(csp.namespaced(generateMemoryModuleRef(seq), LIFT_PROCESS), process);
	}

	/**
	 * Gets a reference to the memory channel of variable v.
	 *
	 * This reference is relative to the parametric sequence group body, so
	 * it assumes that we can traverse backwards through the binding to its
	 * sequence.
	 *
	 * @param v  the binding for which we want a memory channel.
	 *
	 * @return  the partially-specified CSP-M memory channel.
	 */
	public CharSequence generateChannelRef(Variable v) {
		return csp.namespaced(generateMemoryModuleRef(getSequence(v)), v.getName());
	}

	private CharSequence generateMemoryModuleRef(Interaction it) {
		return csp.namespaced(SpecGroupParametricField.MEMORY_MODULE.toString(), memoryModuleName(it));
	}

	private CharSequence memoryModuleName(Interaction it) {
		return Optional.ofNullable(it).map(Interaction::getName).orElse("unknown");
	}

	private Interaction getSequence(Variable v) {
		// TODO(@MattWindsor91): eventually this should be anything that can hold the parent variable.
		return EcoreUtil2.getContainerOfType(v, Interaction.class);
	}

	private CharSequence generatePublicBody(VariableList it) {
		return """
-- Get/set channels
%s
		
%s(P) = (
  P [| %s |] %s
) \\ %s
""".formatted(generateChannelDefinitions(it), LIFT_PROCESS, SYNC_SET, generateInitialRun(it), SYNC_SET);
	}

	/**
	 * Generates the channel set for a memory.
	 *
	 * As mentioned elsewhere, the interface between the memory process and
	 * the definition process is a series of hidden in/out channels, one per
	 * memory slot.  This method generates their CSP-M definitions.
	 */
	private CharSequence generateChannelDefinitions(VariableList it) {
		return it.getVars().stream().map(this::generateChannelDefinition).collect(Collectors.joining("\n"));
	}

	private CharSequence generateChannelDefinition(Variable it) {
		return "channel %s : %s.%s".formatted(it.getName(),MEM_OP_TYPE, typeGen.compileType(it.getType()));
	}

	private CharSequence generatePrivateBody(VariableList it) {
		return String.join("\n\n", generateSyncSet(it), generateProcess(it));
	}

	private CharSequence generateSyncSet(VariableList it) {
		return csp.definition(SYNC_SET, csp.enumeratedSet(mapOverVariables(it, Variable::getName)));
	}

	private CharSequence generateProcess(VariableList it) {
		return csp.definition(generateProcessHeader(it), generateProcessBody(it));
	}

	private CharSequence generateProcessHeader(VariableList it) {
		return generateRun(it, this::generateHeaderName);
	}

	private CharSequence generateInitialRun(VariableList it) {
		return generateRun(it, x -> gu.typeDefaultValue(x.getType()));
	}

	/**
	 * Generates a header/invocation of the run process for a memory, using
	 * the given function to transform its slots into arguments.
	 *
	 * @param it         the memory for which we are generating.
	 * @param transform  the function to apply to slots to produce arguments.
	 *
	 * @return a header or invocation of the memory's run process, in CSP-M.
	 */
	private CharSequence generateRun(VariableList it, Function<Variable, CharSequence> transform) {
		return csp.function(RUN_PROCESS, mapOverVariables(it, transform));
	}

	// Note that these are the reverse direction from how they appear to
	// processes using the memory.  This may seem obvious, but I got it
	// wrong at first (GitHub issue #80 on robocert-sequences...)

	private CharSequence[] mapOverVariables(VariableList it, Function<Variable, CharSequence> transform) {
		return it.getVars().stream().map(transform).toArray(CharSequence[]::new);
	}

	private CharSequence generateHeaderName(Variable it) {
		return "Bnd_" + it.getName();
	}

	private CharSequence generateProcessBody(VariableList it) {
		return it.getVars().stream().map(s -> generateVariableBody(it, s)).collect(Collectors.joining(" [] "));
	}

	private CharSequence generateVariableBody(VariableList m, Variable s) {
		return """
(
	%s -> %s
[]
	%s -> %s
)
""".formatted(generateVariableIn(s), generateProcessHeader(m), generateVariableOut(s), generateProcessHeader(m));
	}

	private CharSequence generateVariableIn(Variable it) {
		return "%s.set?%s".formatted(it.getName(), generateHeaderName(it));
	}

	private CharSequence generateVariableOut(Variable it) {
		return "%s.get!%s".formatted(it.getName(), generateHeaderName(it));
	}
}
