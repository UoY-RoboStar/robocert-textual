/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
package robostar.robocert.textual.generator.tockcsp.seq.fragment;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Collector;

import robostar.robocert.textual.generator.intf.seq.ContextualGenerator;
import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.AltFragment;
import robostar.robocert.BranchFragment;
import robostar.robocert.InteractionOperand;
import robostar.robocert.ParFragment;
import robostar.robocert.XAltFragment;

/**
 * Generator for branch fragments.
 *
 * @author Matt Windsor
 */
public record BranchFragmentGenerator(CSPStructureGenerator csp,
                                      ContextualGenerator<InteractionOperand> bg) {

  @Inject
  public BranchFragmentGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(bg);
  }

  /**
   * Generates CSP-M for a branch step.
   *
   * @param b   branch step to generate.
   * @param ctx context of the lifeline for which we are generating CSP-M.
   * @return the generated CSP-M process.
   */
  public CharSequence generate(BranchFragment b, LifelineContext ctx) {
    CharSequence body = b.getBranches().parallelStream().map(x -> csp.tuple(bg.generate(x, ctx)))
        .collect(operator(b));

    if (b instanceof ParFragment p) {
      body = addParSynchronisation(p, body, ctx);
    }

    return csp.commented(comment(b), csp.tuple(body));
  }

  private CharSequence addParSynchronisation(ParFragment p, CharSequence body, LifelineContext ctx) {
    // TODO(@MattWindsor91): GitHub #128: Lima et al. add this synchronisation, but UML doesn't?
    final var global = ctx.global();
    return global.parChannelIfNeeded().map(ch -> {
      final var i = global.pars().fragments().indexOf(p);
      final var header = csp.function("ParSync", ch, Integer.toString(i));
      return csp.function(header, body);
    }).orElse(body);
  }

  /**
   * Gets a debug comment corresponding to the branch step.
   *
   * @param b the step to generate.
   * @return the comment.
   */
  private CharSequence comment(BranchFragment b) {
    if (b instanceof ParFragment) {
      return "par";
    }
    if (b instanceof AltFragment) {
      return "alt";
    }
    if (b instanceof XAltFragment) {
      return "xalt";
    }
    // This will result in an exception later anyway.
    return "?";
  }

  /**
   * Gets the CSP-M operator corresponding to the branch step.
   *
   * @param b the step to generate.
   * @return the corresponding operator as a collector over branch bodies.
   */
  private Collector<CharSequence, ?, String> operator(BranchFragment b) {
    final var cb = csp.bins();
    if (b instanceof ParFragment) {
      return cb.toInterleave();
    }
    if (b instanceof AltFragment) {
      return cb.toIntChoice();
    }
    if (b instanceof XAltFragment) {
      return cb.toExtChoice();
    }
    throw new IllegalArgumentException("unsupported branch operator: %s".formatted(b));
  }
}
