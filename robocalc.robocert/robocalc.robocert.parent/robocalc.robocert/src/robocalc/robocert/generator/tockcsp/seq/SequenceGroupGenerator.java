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

import com.google.inject.Inject;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.core.SpecGroupGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageSetGenerator;
import robocalc.robocert.generator.utils.MemoryFactory;
import robocalc.robocert.model.robocert.Interaction;
import robocalc.robocert.model.robocert.SequenceGroup;

/**
 * Generator for {@link SequenceGroup}s.
 *
 * @author Matt Windsor
 */
public class SequenceGroupGenerator extends SpecGroupGenerator<SequenceGroup> {
  @Inject private CSPStructureGenerator csp;
  @Inject private InteractionGenerator sg;
  @Inject private MessageSetGenerator msg;
  @Inject private MemoryFactory mf;
  @Inject private ModuleGenerator mg;

  @Override
  protected Stream<CharSequence> generatePrivateElements(SequenceGroup group) {
    return Stream.of(msg.generateNamedSets(group.getMessageSets(), group.getTarget()));
  }

  @Override
  protected CharSequence typeName(SequenceGroup group) {
    return "SEQUENCE";
  }

  @Override
  protected Stream<CharSequence> openDefBodyElements(SequenceGroup it) {
    final var seqs = it.getInteractions();
    return Stream.concat(memModule(seqs), seqModule(seqs));
  }

  private Stream<CharSequence> memModule(EList<Interaction> sequences) {
    return mf.buildMemories(sequences.stream())
        .map(mg::generate)
        .collect(collectToModule(SpecGroupParametricField.MEMORY_MODULE));
  }

  private Stream<CharSequence> seqModule(EList<Interaction> sequences) {
    return sequences.stream()
        .map(this::seqDef)
        .collect(collectToModule(SpecGroupParametricField.SEQUENCE_MODULE));
  }

  private CharSequence seqDef(Interaction s) {
    return csp.definition(s.getName(), sg.generate(s));
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
