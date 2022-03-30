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

import com.google.inject.Inject;
import java.util.Objects;
import java.util.function.Consumer;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.utils.MessageSetOptimiser;
import robocalc.robocert.model.robocert.BinaryMessageSet;
import robocalc.robocert.model.robocert.BinarySetOperator;
import robocalc.robocert.model.robocert.ExtensionalMessageSet;
import robocalc.robocert.model.robocert.MessageSet;
import robocalc.robocert.model.robocert.RefMessageSet;
import robocalc.robocert.model.robocert.UniverseMessageSet;

/**
 * CSP generator for message sets.
 *
 * @author Matt Windsor
 */
public record MessageSetGenerator(CSPStructureGenerator csp, MessageSetOptimiser mso,
                                  MessageGenerator msg) {
  // TODO(@MattWindsor91): split named set functionality out of this.

  /**
   * The name of the message set module exposed by RoboCert.
   */
  public static final CharSequence MODULE_NAME = "MsgSets";
  /**
   * The name of the universe set exposed by RoboCert in the message set module.
   */
  public static final CharSequence UNIVERSE_NAME = "Universe";

  /**
   * Constructs a message set generator.
   *
   * @param csp generator for low-level CSP-M structure.
   * @param mso optimiser for message sets, used for named message set generation.
   * @param msg generator for message specs.
   */
  @Inject
  public MessageSetGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(mso);
    Objects.requireNonNull(msg);
  }

  /**
   * Optimises a message set, then generates CSP-M for it.
   *
   * @param m         the set in question.
   * @param registrar function that receives the optimised set before generation, and which should
   *                  re-attach the new set to the object graph in place of m (eg, if m is
   *                  <code>foo.getSet()</code>, registrar should be <code>foo::setSet</code>).
   * @return generated, optimised CSP for the message set.
   */
  public CharSequence optimiseAndGenerate(MessageSet m, Consumer<MessageSet> registrar) {
    final var opt = mso.optimise(m);
    registrar.accept(opt);
    return generate(opt);
  }

  /**
   * Generates a CSP event set for message set.
   *
   * @param m the set in question.
   * @return generated CSP for the message set.
   * @apiNote This generator does not optimise sets before generating them; it just emits the direct
   * CSP equivalent of the set definition.
   */
  public CharSequence generate(MessageSet m) {
    if (m instanceof UniverseMessageSet) {
      return qualifiedUniverseName();
    }
    if (m instanceof ExtensionalMessageSet e) {
      return msg.generateBulkCSPEventSet(e.getMessages());
    }
    if (m instanceof RefMessageSet r) {
      return csp.namespaced(MODULE_NAME, r.getSet().getName());
    }
    if (m instanceof BinaryMessageSet b) {
      return csp.function(generateOp(b.getOperator()), generate(b.getLhs()), generate(b.getRhs()));
    }
    throw new IllegalArgumentException("unexpected message set type: %s".formatted(m));
  }

  private CharSequence generateOp(BinarySetOperator op) {
    return switch (op) {
      case UNION -> "union";
      case INTERSECTION -> "inter";
      case DIFFERENCE -> "diff";
    };
  }

  public CharSequence qualifiedUniverseName() {
    return csp.namespaced(MODULE_NAME, UNIVERSE_NAME);
  }
}
