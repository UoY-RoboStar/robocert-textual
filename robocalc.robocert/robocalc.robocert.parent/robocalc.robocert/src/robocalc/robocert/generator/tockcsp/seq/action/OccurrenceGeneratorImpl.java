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
package robocalc.robocert.generator.tockcsp.seq.action;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.seq.OccurrenceGenerator;
import robocalc.robocert.generator.intf.seq.LifelineContext;
import robocalc.robocert.generator.tockcsp.core.ExpressionGenerator;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.generator.tockcsp.seq.message.MessageGenerator;
import robocalc.robocert.model.robocert.MessageOccurrence;
import robocalc.robocert.model.robocert.FinalAction;
import robocalc.robocert.model.robocert.Occurrence;
import robocalc.robocert.model.robocert.WaitAction;

/**
 * Top-level CSP generator for sequence actions.
 *
 * @author Matt Windsor
 */
public record OccurrenceGeneratorImpl(CSPStructureGenerator csp,
                                      ExpressionGenerator eg,
                                      LoadStoreGenerator lsg,
                                      MessageGenerator msg) implements
    OccurrenceGenerator {

  /**
   * Constructs an action generator.
   *
   * @param csp a CSP structure generator, used for producing common CSP-M fragments.
   * @param eg  an expression generator.
   * @param lsg a load and store generator.
   * @param msg a message spec generator, used for arrow actions.
   */
  @Inject
  public OccurrenceGeneratorImpl {
  }

  /**
   * Generates CSP-M for an occurrence.
   *
   * @param a   the occurrence.
   * @param ctx context for the current lifeline.
   * @return the generated CSP.
   */
  public CharSequence generate(Occurrence a, LifelineContext ctx) {
    // TODO(@MattWindsor91): use lifeline context.

    if (a instanceof MessageOccurrence r) {
      return generateArrow(r);
    }
    // TODO(@MattWindsor91): one day, possibly more than one type of final action.
    if (a instanceof FinalAction) {
      return "STOP";
    }
    if (a instanceof WaitAction w) {
      return generateWait(w);
    }
    throw new IllegalArgumentException("unsupported sequence action: %s".formatted(a));
  }

  private CharSequence generateArrow(MessageOccurrence r) {
    // TODO(@MattWindsor91): This should really be in the CSPStructureGenerator... somehow.
    return "%s -> %sSKIP".formatted(msg.generatePrefix(r.getMessage()), lsg.generateBindingStores(r));
  }

  private CharSequence generateWait(WaitAction w) {
    // This is in the tock-CSP standard library.
    return csp.function("WAIT", eg.generate(w.getUnits()));
  }
}
