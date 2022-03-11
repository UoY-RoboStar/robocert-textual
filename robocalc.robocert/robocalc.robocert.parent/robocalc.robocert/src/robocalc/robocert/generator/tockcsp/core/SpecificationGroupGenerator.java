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

import com.google.inject.Inject;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.tockcsp.seq.InteractionGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
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
  @Inject private CSPStructureGenerator csp;
  @Inject private TargetBodyGenerator tg;
  @Inject private InteractionGenerator sg;
  @Inject private MessageSetGenerator msg;
  @Inject private ModuleGenerator mg;

  @Override
  protected Stream<CharSequence> generateBodyElements(SpecificationGroup group) {
    return Stream.of(openDef(group), closedDef(group), tickTockContext());
  }

  @Override
  protected boolean isInModule(SpecificationGroup group) {
    // Spec groups always generate a module.
    return true;
  }

  /**
   * Generates a process definition for the 'closed' form of this group.
   *
   * <p>The closed form has no parameters, with all constants assigned values either from its
   * target's instantiation or from the top-level instantiations.csp file.
   *
   * @param it the group for which we are generating a closed form.
   * @return CSP defining the 'closed' form of this group.
   */
  private CharSequence closedDef(SpecificationGroup it) {
    return csp.instance(
        SpecGroupField.PARAMETRIC_CLOSED.toString(), openSig(it, it.getAssignments()));
  }

  /**
   * Generates an external reference for the 'open' form of this group.
   *
   * <p>The open form has parameters exposed, and any reference to it must fill those parameters
   * using either values in the given instantiation or, where values are missing, references to the
   * instantiations CSP file.
   *
   * @param it the group for which we are generating CSP.
   * @param inst the instantiation for this 'open' form.
   * @return generated CSP for referring to the 'open' form of this group.
   */
  public CharSequence generateOpenRef(SpecificationGroup it, List<ConstAssignment> instantiation) {
    return csp.function(openRefName(it), openSigParams(it, instantiation));
  }

  private CharSequence openRefName(SpecificationGroup it) {
    return csp.namespaced(it.getName(), SpecGroupField.PARAMETRIC_OPEN.toString());
  }

  /**
   * Generates a process definition for the 'open' form of this target.
   *
   * @param it the group for which we are generating an open form.
   * @return generated CSP for the 'open' form of a spec group.
   */
  private CharSequence openDef(SpecificationGroup it) {
    return csp.module(openSig(it, null), openDefBody(it));
  }

  /**
   * Generates the body of this specification's 'open' form.
   *
   * @implNote This merges elements from two sources: a set common to all specifications (such as
   *     the target definition), and a set overridden by the downstream class.
   * @param it the group for which we are generating an open form.
   * @return CSP-M for the body of the 'open' form of the spec group.
   */
  private CharSequence openDefBody(SpecificationGroup it) {
    return csp.innerJoin(openDefBodyElements(it));
  }

  /**
   * Generates the set of elements inside the open form.
   *
   * @param it the group for which we are generating an open form.
   * @return CSP-M for common elements of the body of the 'open' form of the spec group.
   */
  private Stream<CharSequence> openDefBodyElements(SpecificationGroup it) {
    // Space here for expansion.
    return Stream.concat(Stream.of(targetDef(it)), specificationElements(it));
  }

  private CharSequence targetDef(SpecificationGroup it) {
    // NOTE(@MattWindsor91): as far as I know, this needn't be timed
    return csp.definition(SpecGroupParametricField.TARGET.toString(), targetDefBody(it));
  }

  private CharSequence targetDefBody(SpecificationGroup it) {
    return csp.function(
        tg.generateDef(it.getTarget()),
        tg.generateRefParams(it.getTarget(), null, it.getAssignments(), true));
  }

  private CharSequence tickTockContext() {
    // TODO(@MattWindsor91): restrict from the universe?
    return csp.instance(
        SpecGroupField.TICK_TOCK_CONTEXT.toString(), csp.function("model_shifting", universe()));
  }

  private CharSequence universe() {
    return csp.namespaced(
        SpecGroupField.MESSAGE_SET_MODULE.toString(), MessageSetGenerator.UNIVERSE_NAME);
  }

  /**
   * Generates the signature of an open sequence group definition or reference.
   *
   * <p>Because the parameters used in the definition are just the constant IDs, which are also how
   * we refer to any fallback references to the instantiations file, both definitions and references
   * can have the same signature generator.
   *
   * @param group the group for which we are generating an open form.
   * @param outerInst any instantiation being applied at the outer level (may be null).
   * @return CSP referring to, or giving the signature of, the 'open' form of this group.
   */
  private CharSequence openSig(SpecificationGroup group, List<ConstAssignment> outerInst) {
    return csp.function(SpecGroupField.PARAMETRIC_OPEN.toString(), openSigParams(group, outerInst));
  }

  private CharSequence[] openSigParams(SpecificationGroup group, List<ConstAssignment> outerInst) {
    return tg.generateRefParams(group.getTarget(), group.getAssignments(), outerInst, false);
  }

  @Override
  protected CharSequence typeName(SpecificationGroup group) {
    return "SPECIFICATION";
  }

  @Override
  protected Stream<CharSequence> generatePrivateElements(SpecificationGroup group) {
    return Stream.of(msg.generateNamedSets(group));
  }

  protected Stream<CharSequence> specificationElements(SpecificationGroup group) {
    final var specs = group.getInteractions();
    return Stream.concat(memModule(specs), specModule(specs));
  }

  private Stream<CharSequence> memModule(EList<Interaction> sequences) {
    return sequences.stream()
        .map(Interaction::getVariables)
        .filter(x -> x != null && !x.getVars().isEmpty())
        .map(mg::generate)
        .collect(collectToModule(SpecGroupParametricField.MEMORY_MODULE));
  }

  private Stream<CharSequence> specModule(EList<Interaction> sequences) {
    return sequences.stream()
        .map(this::specDef)
        .collect(collectToModule(SpecGroupParametricField.SEQUENCE_MODULE));
  }

  private CharSequence specDef(Interaction i) {
    return csp.definition(i.getName(), sg.generate(i));
  }

  /**
   * Joins a stream of character sequences with newlines, then wraps the result in a parametric
   * field module only if the resulting sequence is empty.
   *
   * @param field the field corresponding to the module to produce.
   * @return an empty Stream if there is no module; else, a singleton Stream containing the
   *     character sequence.
   */
  private Collector<CharSequence, ?, Stream<CharSequence>> collectToModule(
      SpecGroupParametricField field) {
    return Collectors.collectingAndThen(Collectors.joining("\n"), x -> moduleIfNonEmpty(x, field));
  }

  private Stream<CharSequence> moduleIfNonEmpty(CharSequence mod, SpecGroupParametricField field) {
    // TODO(@MattWindsor91): this is a slightly awkward quickfix?
    final var name = field.toString();
    final var isTimed = field == SpecGroupParametricField.SEQUENCE_MODULE;

    return Stream.of(mod)
        .filter(x -> !x.isEmpty())
        .map(x -> csp.module(name, csp.timedIf(isTimed, x)));
  }
}
