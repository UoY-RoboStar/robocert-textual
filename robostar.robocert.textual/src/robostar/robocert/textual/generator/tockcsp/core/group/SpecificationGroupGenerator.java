/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.core.group;

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import robostar.robocert.textual.generator.intf.core.SpecGroupField;
import robostar.robocert.textual.generator.intf.core.SpecGroupParametricField;
import robostar.robocert.textual.generator.intf.seq.context.InteractionContext;
import robostar.robocert.textual.generator.tockcsp.core.tgt.OverrideGenerator;
import robostar.robocert.textual.generator.tockcsp.core.tgt.TargetGenerator;
import robostar.robocert.textual.generator.tockcsp.core.tgt.UniverseGenerator;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStreamHelper;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.memory.ModuleGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.ActorGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.InteractionGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.LifelineContextFactory;
import robostar.robocert.textual.generator.tockcsp.seq.SyncChannelGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.message.NamedSetModuleGenerator;
import robostar.robocert.textual.generator.utils.param.TargetParameterResolver;
import robostar.robocert.CollectionTarget;
import robostar.robocert.ConstAssignment;
import robostar.robocert.Interaction;
import robostar.robocert.SpecificationGroup;

/**
 * Generator for specification groups.
 *
 * <p>In the CSP semantics, a specification group contains two submodules: a parametric 'open'
 * definition (taking the target parameterisation) and a non-parametric 'closed' definition (
 *
 * @author Matt Windsor
 */
public class SpecificationGroupGenerator extends GroupGenerator<SpecificationGroup> {

  // TODO(@MattWindsor91): this class is getting very large and dependent on a lot of things.
  // Maybe break up along subnotation lines.

  @Inject
  private CSPStructureGenerator csp;
  @Inject
  private CSPStreamHelper cspStream;
  @Inject
  private TargetGenerator targetGen;
  @Inject
  private UniverseGenerator univGen;
  @Inject
  private InteractionGenerator interactionGen;
  @Inject
  private NamedSetModuleGenerator msgSetGen;
  @Inject
  private SyncChannelGenerator syncGen;
  @Inject
  private ModuleGenerator memoryGen;
  @Inject
  private ActorGenerator actorGen;
  @Inject
  private OverrideGenerator overrideGen;
  @Inject
  private TargetParameterResolver paramRes;
  @Inject
  private LifelineContextFactory ctxFactory;

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
    final var specs = group.getInteractions().stream().map(ctxFactory::context).toList();

    final var optimisations = Stream.of("sbisim", "dbisim").map(x -> "transparent " + x);

    // Component targets are just invocations of the existing RoboChart process semantics, and
    // don't need to be wrapped in a timed section.  Collection targets are more involved, and do.
    final var target = csp.timedIf(group.getTarget() instanceof CollectionTarget,
        csp.definition(SpecGroupParametricField.TARGET.toString(),
            targetGen.openDef(group.getTarget())));

    final var elements = Streams.concat(optimisations, Stream.of(target),
        msgSetGen.generate(group).stream(), channelModule(specs).stream(), interactionModule(specs).stream());

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
        csp.namespaced(univGen.generate(group.getTarget())));

    final var specs = group.getInteractions();
    return Streams.concat(Stream.of(overrides, universe), memModule(specs).stream(),
        actorModule(group).stream());
  }

  private Optional<CharSequence> channelModule(List<InteractionContext> seqContexts) {
    // TODO(@MattWindsor91): other channels?
    return seqContexts.stream().flatMap(syncGen::generate).collect(
        cspStream.collectToModule(SpecGroupField.CHANNEL_MODULE.toString(), false));
  }

  private Optional<CharSequence> memModule(EList<Interaction> sequences) {
    return sequences.stream().map(Interaction::getVariables).filter(memoryGen::needsMemory)
        .map(memoryGen::generate).collect(
            cspStream.collectToModule(SpecGroupParametricField.MEMORY_MODULE.toString(), false));
  }

  private Optional<CharSequence> interactionModule(List<InteractionContext> seqs) {
    return seqs.stream().map(this::specDef).collect(
        cspStream.collectToModule(SpecGroupParametricField.INTERACTION_MODULE.toString(), true));
  }

  private CharSequence specDef(InteractionContext i) {
    return csp.definition(i.seq().getName(), interactionGen.generate(i));
  }

}
