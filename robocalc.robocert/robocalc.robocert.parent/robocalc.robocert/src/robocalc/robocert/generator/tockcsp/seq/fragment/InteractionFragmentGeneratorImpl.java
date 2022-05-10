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
package robocalc.robocert.generator.tockcsp.seq.fragment;

import circus.robocalc.robochart.Variable;

import com.google.inject.Inject;
import java.util.stream.Stream;
import java.util.Optional;
import robocalc.robocert.generator.intf.seq.InteractionFragmentGenerator;
import robocalc.robocert.generator.intf.seq.context.LifelineContext;
import robocalc.robocert.generator.intf.seq.fragment.BlockFragmentGenerator;
import robocalc.robocert.generator.tockcsp.memory.LoadStoreGenerator;
import robocalc.robocert.model.robocert.BlockFragment;
import robocalc.robocert.model.robocert.BranchFragment;
import robocalc.robocert.model.robocert.InteractionFragment;
import robocalc.robocert.model.robocert.LoopFragment;
import robocalc.robocert.model.robocert.OccurrenceFragment;

/**
 * Generator for interaction fragments.
 * <p>
 * This generator mainly just delegates into lower-level generators, but also handles variable
 * loads.
 *
 * @author Matt Windsor
 */
public record InteractionFragmentGeneratorImpl(OccurrenceFragmentGenerator ag,
                                               BlockFragmentGenerator blockGen,
                                               BranchFragmentGenerator branchGen,
                                               LoadStoreGenerator ls) implements
    InteractionFragmentGenerator {

  @Inject
  public InteractionFragmentGeneratorImpl {
  }

  @Override
  public CharSequence generate(InteractionFragment f, LifelineContext ctx) {
    return generateLoads(f) + generateAfterLoads(f, ctx);
  }

  private String generateLoads(InteractionFragment f) {
    return ls.generateLoads(directlyReferencedVariables(f)).toString();
  }

  private Stream<Variable> directlyReferencedVariables(InteractionFragment f) {
    // TODO(@MattWindsor91): make this part of the metamodel?
    if (f instanceof OccurrenceFragment a) {
      return ls.getExprVariables(a);
    }
    if (f instanceof BranchFragment b) {
      return b.getBranches().stream().flatMap(x -> ls.getExprVariables(x.getGuard()));
    }
    // Note that LoopFragments are a form of BlockFragment.
    if (f instanceof LoopFragment l) {
      // The loop may have a bound, whose expressions we'll need to load.
      final var boundVars = Optional.ofNullable(l.getBound()).stream()
          .flatMap(ls::getExprVariables);
      return Stream.concat(boundVars, ls.getExprVariables(l.getBody().getGuard()));
    }
    if (f instanceof BlockFragment b) {
      return ls.getExprVariables(b.getBody().getGuard());
    }
    return Stream.empty();
  }

  private CharSequence generateAfterLoads(InteractionFragment f, LifelineContext ctx) {
    // Remember to extend this with any new top-level fragment types added to the metamodel.
    if (f instanceof OccurrenceFragment a) {
      return ag.generate(a, ctx);
    }
    if (f instanceof BranchFragment b) {
      return branchGen.generate(b, ctx);
    }
    if (f instanceof BlockFragment b) {
      return blockGen.generate(b, ctx);
    }
    throw new IllegalArgumentException("unsupported fragment type: %s".formatted(f));
  }
}