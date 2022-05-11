/*******************************************************************************
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
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.seq.message;

import circus.robocalc.robochart.generator.csp.untimed.TypeGenerator;
import com.google.inject.Inject;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.xtext.xbase.lib.Pair;
import robocalc.robocert.generator.tockcsp.core.TemporaryVariableGenerator;
import robostar.robocert.MessageTopic;
import robostar.robocert.WildcardValueSpecification;

/**
 * Generates CSP for ranges of arguments, which will go into a set comprehension inside the
 * generated message.
 *
 * @author Matt Windsor
 */
public record ArgumentRangeGenerator(TemporaryVariableGenerator bg,
																		 TypeGenerator tg) {

  @Inject
  public ArgumentRangeGenerator {
  }

  /**
   * Generates the set comprehension ranges for a set of arguments, using the given topic to resolve
   * types.
   *
   * @param t    the topic for which we are generating ranges.
   * @param args an iterable of pairs of index in the message argument list, and wildcard argument
   *             to expand into a comprehension.
   * @return CSP-M for the set comprehension, less any set delimiters.
   */
  public CharSequence generateRanges(MessageTopic t,
      Stream<Pair<Long, WildcardValueSpecification>> args) {
    return args.map(p -> generateRange(t, p.getValue(), p.getKey()))
        .collect(Collectors.joining(", "));
  }

  private CharSequence generateRange(MessageTopic t, WildcardValueSpecification arg, long index) {
    final var name = bg.generateArgumentName(arg.getDestination(), index);
    final var ty = tg.compileType(t.getParamTypes().get((int) index));
    return "%s <- %s".formatted(name, ty);
  }
}
