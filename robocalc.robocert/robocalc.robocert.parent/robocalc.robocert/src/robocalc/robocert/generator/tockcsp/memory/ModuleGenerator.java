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
import robocalc.robocert.generator.utils.MemoryFactory.Memory;
import robocalc.robocert.generator.utils.MemoryFactory.Memory.Slot;
import robocalc.robocert.generator.utils.name.BindingNamer;
import robocalc.robocert.model.robocert.Binding;
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
public record ModuleGenerator(CTimedGeneratorUtils gu, CSPStructureGenerator csp, TypeGenerator typeGen, BindingNamer bindingNamer) {
	private static final String MEM_OP_TYPE = "MemOp"; // in robocert_defs
	private static final String LIFT_PROCESS = "lift";
	private static final String RUN_PROCESS = "proc";
	private static final String SYNC_SET = "sync";

	@Inject
	public ModuleGenerator {
		Objects.requireNonNull(gu);
		Objects.requireNonNull(csp);
		Objects.requireNonNull(typeGen);
		Objects.requireNonNull(bindingNamer);
	}

	/**
	 * Generates the module for a memory.
	 *
	 * @param it  the memory to generate.
	 *
	 * @return  a CSP-M module containing the memory definition (channels,
	 *          process, and so on).
	 */
	public CharSequence generate(Memory it) {
		return csp.moduleWithPrivate(name(it), generatePrivateBody(it), generatePublicBody(it));
	}

	private String name(Memory it) {
		final var parent = it.parent();
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
	 * Gets a reference to the memory channel of binding b.
	 *
	 * This reference is relative to the parametric sequence group body, so
	 * it assumes that we can traverse backwards through the binding to its
	 * sequence.
	 *
	 * @param b  the binding for which we want a memory channel.
	 *
	 * @return  the partially-specified CSP-M memory channel.
	 */
	public CharSequence generateChannelRef(Binding b) {
		return csp.namespaced(generateMemoryModuleRef(getSequence(b)), bindingNamer.getUnambiguousName(b));
	}

	private CharSequence generateMemoryModuleRef(Interaction it) {
		return csp.namespaced(SpecGroupParametricField.MEMORY_MODULE.toString(), memoryModuleName(it));
	}

	private CharSequence memoryModuleName(Interaction it) {
		return Optional.ofNullable(it).map(Interaction::getName).orElse("unknown");
	}

	private Interaction getSequence(Binding b) {
		return EcoreUtil2.getContainerOfType(b, Interaction.class);
	}

	private CharSequence generatePublicBody(Memory it) {
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
	private CharSequence generateChannelDefinitions(Memory it) {
		return it.slots().stream().map(this::generateChannelDefinition).collect(Collectors.joining("\n"));
	}

	private CharSequence generateChannelDefinition(Memory.Slot it) {
		return "channel %s {- %s -} : %s.%s".formatted(it.unambiguousName(), it.binding().getName(), MEM_OP_TYPE, typeGen.compileType(it.type()));
	}

	private CharSequence generatePrivateBody(Memory it) {
		return String.join("\n\n", generateSyncSet(it), generateProcess(it));
	}

	private CharSequence generateSyncSet(Memory it) {
		return csp.definition(SYNC_SET, csp.enumeratedSet(mapOverSlots(it, Slot::unambiguousName)));
	}

	private CharSequence generateProcess(Memory it) {
		return csp.definition(generateProcessHeader(it), generateProcessBody(it));
	}

	private CharSequence generateProcessHeader(Memory it) {
		return generateRun(it, this::generateHeaderName);
	}

	private CharSequence generateInitialRun(Memory it) {
		return generateRun(it, (x) -> gu.typeDefaultValue(x.type()));
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
	private CharSequence generateRun(Memory it, Function<Memory.Slot, CharSequence> transform) {
		return csp.function(RUN_PROCESS, mapOverSlots(it, transform));
	}

	// Note that these are the reverse direction from how they appear to
	// processes using the memory.  This may seem obvious, but I got it
	// wrong at first (GitHub issue #80 on robocert-sequences...)

	private CharSequence[] mapOverSlots(Memory it, Function<Slot, CharSequence> transform) {
		return it.slots().stream().map(transform).toArray(CharSequence[]::new);
	}

	private CharSequence generateHeaderName(Memory.Slot it) {
		return "Bnd_" + it.unambiguousName();
	}

	private CharSequence generateProcessBody(Memory it) {
		return it.slots().stream().map((s) -> generateSlotEntry(it, s)).collect(Collectors.joining(" [] "));
	}

	private CharSequence generateSlotEntry(Memory m, Slot s) {
		return """
(
	%s -> %s
[]
	%s -> %s
)
""".formatted(generateSlotIn(s), generateProcessHeader(m), generateSlotOut(s), generateProcessHeader(m));
	}

	private CharSequence generateSlotIn(Memory.Slot it) {
		return "%s.set?%s".formatted(it.unambiguousName(), generateHeaderName(it));
	}

	private CharSequence generateSlotOut(Memory.Slot it) {
		return "%s.get!%s".formatted(it.unambiguousName(), generateHeaderName(it));
	}
}
