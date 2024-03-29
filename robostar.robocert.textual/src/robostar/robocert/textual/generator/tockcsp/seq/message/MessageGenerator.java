/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq.message;

import com.google.common.collect.Streams;
import com.google.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.xtext.xbase.lib.Pair;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.textual.generator.tockcsp.seq.ArgumentGenerator;
import robostar.robocert.Message;
import robostar.robocert.WildcardValueSpecification;

/**
 * Generates CSP for various aspects of message specs.
 *
 * @author Matt Windsor
 */
public record MessageGenerator(CSPStructureGenerator csp, ArgumentRangeGenerator rangeGen,
                               ArgumentGenerator argGen, TopicGenerator topicGen) {

  /**
   * Constructs a message spec generator.
   *
   * @param csp      CSP structure generator.
   * @param rangeGen argument range generator.
   * @param argGen   argument generator.
   * @param topicGen generates topics from channels.
   */
  @Inject
  public MessageGenerator {
  }

  /**
   * Generates a CSP prefix for one message spec.
   *
   * @param spec the spec for which we are generating CSP.
   * @return generated CSP for the message spec.
   */
  public CharSequence generatePrefix(Message spec) {
    return String.join("", generateChannel(spec), generateArguments(spec));
  }

  /**
   * Generates a CSP event set for multiple message specs.
   *
   * @param it the specs for which we are generating CSP (may be null)
   * @return generated CSP for the event set of multiple message spec.
   */
  public CharSequence generateBulkCSPEventSet(List<Message> it) {
    // TODO(@MattWindsor91): generalise this efficient bulk set generation.
    if (it == null || it.isEmpty()) {
      return csp.set();
    }
    return switch (it.size()) {
      case 1 -> generateCSPEventSet(it.get(0));
      case 2 -> generatePairCSPEventSet(it.get(0), it.get(1));
      default -> generateManyCSPEventSet(it);
    };
  }

  private CharSequence generatePairCSPEventSet(Message fst, Message snd) {
    return csp.union(generateCSPEventSet(fst), generateCSPEventSet(snd));
  }

  private CharSequence generateManyCSPEventSet(List<Message> it) {
    final var sets = it.stream().map(this::generateCSPEventSet).toArray(CharSequence[]::new);
    return csp.iteratedUnion(csp.set(sets));
  }

  /**
   * Generates a CSP event set for a message spec.
   *
   * @param it the spec for which we are generating CSP.
   * @return generated CSP for the event set of one message spec.
   */
  public CharSequence generateCSPEventSet(Message it) {
    // TODO(@MattWindsor91): optimise this some more

    final var wcs = wildcards(it);
    if (wcs.isEmpty()) {
      return csp.enumeratedSet(generatePrefix(it));
    }

    return csp.setComprehension(generateCSPEventSetComprehensionLHS(it),
        rangeGen.generateRanges(it.getTopic(), wcs.stream()));
  }

  private CharSequence generateCSPEventSetComprehensionLHS(Message spec) {
    return String.join("", generateChannel(spec), generateArgumentsForSet(spec));
  }

  private CharSequence generateChannel(Message spec) {
    return topicGen.generate(spec.getTopic(), spec.getFrom(), spec.getTo());
  }

  private CharSequence generateArguments(Message spec) {
    return spec.getArguments().stream().map(argGen::generateForPrefix)
        .collect(Collectors.joining());
  }

  private CharSequence generateArgumentsForSet(Message spec) {
    //noinspection UnstableApiUsage
    return Streams.mapWithIndex(spec.getArguments().stream(), argGen::generateForSet)
        .collect(Collectors.joining());
  }

  // TODO(@MattWindsor91): reimplement filler, simplifications
  private List<Pair<Long, WildcardValueSpecification>> wildcards(Message it) {
    // Indexes must be positions in the whole argument list, not just binding ones
    // so we can't move 'indexed' later in the chain.

    //noinspection UnstableApiUsage
    return Streams.mapWithIndex(it.getArguments().stream(),
            (v, k) -> v instanceof WildcardValueSpecification w ? Pair.of(k, w) : null)
        .filter(Objects::nonNull).toList();
  }
}
