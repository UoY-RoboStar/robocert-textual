/*******************************************************************************
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   $author - initial definition
 ******************************************************************************/
package robocalc.robocert.generator.tockcsp.core.group;

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.core.tgt.OverrideGenerator;
import robocalc.robocert.generator.tockcsp.core.tgt.TargetGenerator;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.tockcsp.seq.ActorGenerator;
import robocalc.robocert.generator.tockcsp.seq.InteractionGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.NamedSetModuleGenerator;
import robocalc.robocert.generator.utils.param.TargetParameterResolver;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.SpecificationGroup;

/**
 * Generator for specification groups.
 *
 * <p>In the CSP semantics, a specification group contains two submodules: a parametric 'open'
 * definition (taking the target parameterisation) and a non-parametric 'closed' definition (
 *
 * @author Matt Windsor
 */
public class SpecificationGroupGenerator extends GroupGenerator<SpecificationGroup> {

  @Inject
  private CSPStructureGenerator csp;
  @Inject
  private TargetGenerator targetGen;
  @Inject
  private InteractionGenerator interactionGen;
  @Inject
  private NamedSetModuleGenerator msgSetGen;
  @Inject
  private ModuleGenerator memoryGen;
  @Inject
  private ActorGenerator actorGen;
  @Inject
  private OverrideGenerator overrideGen;
  @Inject
  private TargetParameterResolver paramRes;

  @Override
  protected Stream<CharSequence> generateBodyElements(SpecificationGroup group) {
    final var openDef = csp.module(openSig(group, null)).withPublic(openDefBody(group)).end();
    final var closedDef = csp.instance(SpecGroupField.PARAMETRIC_CLOSED.toString(),
        openSig(group, group.getAssignments()));
    return Stream.of(openDef, closedDef, tickTockContext());
  }

  @Override
  protected boolean isInModule(SpecificationGroup group) {
    // Spec groups always generate a module.
    return true;
  }

  /**
   * Generates an external reference for the 'open' form of this group.
   *
   * <p>The open form has parameters exposed, and any reference to it must fill those parameters
   * using either values in the given instantiation or, where values are missing, references to the
   * instantiations CSP file.
   *
   * @param group the group for which we are generating CSP.
   * @param inst  the instantiation for this 'open' form.
   * @return generated CSP for referring to the 'open' form of this group.
   */
  public CharSequence generateOpenRef(SpecificationGroup group, List<ConstAssignment> inst) {
    return csp.function(openRefName(group), openSigParams(group, inst));
  }

  private CharSequence openRefName(SpecificationGroup group) {
    return csp.namespaced(group.getName(), SpecGroupField.PARAMETRIC_OPEN.toString());
  }

  /**
   * Generates the body of this specification's 'open' form.
   *
   * @param group the group for which we are generating an open form.
   * @return CSP-M for the body of the 'open' form of the spec group.
   * @implNote This merges elements from two sources: a set common to all specifications (such as
   * the target definition), and a set overridden by the downstream class.
   */
  private CharSequence openDefBody(SpecificationGroup group) {
    // Space here for expansion.
    final var specs = group.getInteractions();

    final var optimisations = Stream.of("sbisim", "dbisim").map(x -> "transparent " + x);

    // Component targets are just invocations of the existing RoboChart processs semantics, and
    // don't need to be wrapped in a timed section.  Collection targets are more involved, and do.
    final var target =
        csp.timedIf(group.getTarget() instanceof CollectionTarget,
          csp.definition(SpecGroupParametricField.TARGET.toString(),
              targetGen.openDef(group.getTarget())));

    final var elements = Streams.concat(
        optimisations,
        Stream.of(target),
        msgSetGen.generate(group).stream(), memModule(specs).stream(), specModule(specs).stream());

    return csp.innerJoin(elements);
  }

  private Optional<CharSequence> actorModule(SpecificationGroup group) {
    return actorGen.generateType(SpecGroupField.ACTOR_ENUM.toString(), group.getActors());
  }

  private CharSequence tickTockContext() {
    // TODO(@MattWindsor91): restrict from the universe?
    return csp.instance(SpecGroupField.TICK_TOCK_CONTEXT.toString(),
        csp.function("model_shifting", SpecGroupField.UNIVERSE.toString()));
  }

  /**
   * Generates the signature of an open sequence group definition or reference.
   *
   * <p>Because the parameters used in the definition are just the constant IDs, which are also how
   * we refer to any fallback references to the instantiations file, both definitions and references
   * can have the same signature generator.
   *
   * @param group     the group for which we are generating an open form.
   * @param outerInst any instantiation being applied at the outer level (can be null).
   * @return CSP referring to, or giving the signature of, the 'open' form of this group.
   */
  private CharSequence openSig(SpecificationGroup group, List<ConstAssignment> outerInst) {
    return csp.function(SpecGroupField.PARAMETRIC_OPEN.toString(), openSigParams(group, outerInst));
  }

  private CharSequence[] openSigParams(SpecificationGroup group, List<ConstAssignment> outerInst) {
    return targetGen.openSigParams(group.getTarget(), group.getAssignments(), outerInst);
  }

  @Override
  protected CharSequence typeName(SpecificationGroup group) {
    return "SPECIFICATION";
  }

  @Override
  protected Stream<CharSequence> generatePrivateElements(SpecificationGroup group) {
    final var overrides = overrideGen.generate(group.getAssignments(),
        paramRes.parameterisation(group.getTarget()).toList());

    // Put only things that don't need to be exposed publicly AND don't depend on the constant
    // instantiation here.
    final var universe = csp.definition(SpecGroupField.UNIVERSE.toString(),
        csp.namespaced(targetGen.semEvents(group.getTarget())));

    return Stream.concat(Stream.of(overrides, universe), actorModule(group).stream());
  }

  private Optional<CharSequence> memModule(EList<Interaction> sequences) {
    return sequences.stream().map(Interaction::getVariables)
        .filter(x -> x != null && !x.getVars().isEmpty()).map(memoryGen::generate)
        .collect(collectToModule(SpecGroupParametricField.MEMORY_MODULE));
  }

  private Optional<CharSequence> specModule(EList<Interaction> sequences) {
    return sequences.stream().map(this::specDef)
        .collect(collectToModule(SpecGroupParametricField.SEQUENCE_MODULE));
  }

  private CharSequence specDef(Interaction i) {
    return csp.definition(i.getName(), interactionGen.generate(i));
  }

  /**
   * Joins a stream of character sequences with newlines, then wraps the result in a parametric
   * field module only if the resulting sequence is non-empty.
   *
   * @param field the field corresponding to the module to produce.
   * @return an empty Stream if there is no module; else, a singleton Stream containing the
   * character sequence.
   */
  private Collector<CharSequence, ?, Optional<CharSequence>> collectToModule(
      SpecGroupParametricField field) {
    return Collectors.collectingAndThen(Collectors.joining("\n"), x -> moduleIfNonEmpty(x, field));
  }

  private Optional<CharSequence> moduleIfNonEmpty(CharSequence mod,
      SpecGroupParametricField field) {
    if (mod.isEmpty()) {
      return Optional.empty();
    }

    final var name = field.toString();
    final var isTimed = field == SpecGroupParametricField.SEQUENCE_MODULE;
    return Optional.of(csp.module(name).withPublic(csp.timedIf(isTimed, mod)).end());
  }
}
