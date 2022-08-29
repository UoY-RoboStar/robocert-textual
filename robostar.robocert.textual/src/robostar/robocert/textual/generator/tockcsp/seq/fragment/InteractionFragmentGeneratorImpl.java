/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.tockcsp.seq.fragment;

import circus.robocalc.robochart.Expression;

import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import robostar.robocert.DeadlockOccurrence;
import robostar.robocert.DiscreteBound;
import robostar.robocert.ExprGuard;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.InteractionOperand;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.Occurrence;
import robostar.robocert.WaitOccurrence;
import robostar.robocert.textual.generator.intf.seq.InteractionFragmentGenerator;
import robostar.robocert.textual.generator.intf.seq.context.LifelineContext;
import robostar.robocert.textual.generator.intf.seq.fragment.BlockFragmentGenerator;
import robostar.robocert.textual.generator.tockcsp.memory.LoadStoreGenerator;
import robostar.robocert.BlockFragment;
import robostar.robocert.BranchFragment;
import robostar.robocert.InteractionFragment;
import robostar.robocert.LoopFragment;
import robostar.robocert.OccurrenceFragment;
import robostar.robocert.util.StreamHelper;

/**
 * Generator for interaction fragments.
 * <p>
 * This generator mainly just delegates into lower-level generators, but also handles variable
 * loads.
 *
 * @author Matt Windsor
 */
public record InteractionFragmentGeneratorImpl(OccurrenceFragmentGenerator fragGen,
                                               BlockFragmentGenerator blockGen,
                                               BranchFragmentGenerator branchGen,
                                               LoadStoreGenerator loadStoreGen) implements
    InteractionFragmentGenerator {

  @Inject
  public InteractionFragmentGeneratorImpl {
    Objects.requireNonNull(blockGen);
    Objects.requireNonNull(branchGen);
    Objects.requireNonNull(fragGen);
    Objects.requireNonNull(loadStoreGen);
  }

  @Override
  public CharSequence generate(InteractionFragment f, LifelineContext ctx) {
    // SEMANTICS: [[ ]]frag

    final var loads = loadStoreGen.generateExprLoads(fragmentExprs(f)).toString();

    return loads + generateBody(f, ctx);
  }

  private Stream<Expression> fragmentExprs(InteractionFragment f) {
    // SEMANTICS: fexprs

    final var ownExprs = directFragmentExprs(f);
    final var guardExprs = operands(f).flatMap(this::guardExprs);

    return Stream.concat(ownExprs, guardExprs);
  }

  private Stream<Expression> guardExprs(InteractionOperand op) {
    return StreamHelper.filter(Stream.of(op.getGuard()), ExprGuard.class).map(ExprGuard::getExpr);
  }

  private Stream<Expression> directFragmentExprs(InteractionFragment f) {
    // SEMANTICS: fdexprs

    // TODO(@MattWindsor91): make this part of the metamodel?
    if (f instanceof OccurrenceFragment o) {
      return directlyReferencedExprs(o.getOccurrence());
    }
    if (f instanceof LoopFragment l) {
      // The loop may have a bound, whose expressions we'll need to load.
      return Stream.ofNullable(l.getBound()).flatMap(this::boundExprs);
    }
    return Stream.empty();
  }

  private Stream<Expression> boundExprs(DiscreteBound b) {
    // SEMANTICS: bexprs

    return Stream.of(b.getLower(), b.getUpper()).filter(Objects::nonNull);
  }

  private Stream<InteractionOperand> operands(InteractionFragment f) {
    // SEMANTICS: fops

    // TODO(@MattWindsor91): move to metamodel?
    if (f instanceof BranchFragment b) {
      return b.getBranches().stream();
    }
    if (f instanceof BlockFragment b) {
      return Stream.of(b.getBody());
    }
    return Stream.empty();
  }

  private Stream<Expression> directlyReferencedExprs(Occurrence occ) {
    if (occ instanceof MessageOccurrence m) {
      return StreamHelper.filter(m.getMessage().getArguments().stream(),
          ExpressionValueSpecification.class).map(ExpressionValueSpecification::getExpr);
    }
    if (occ instanceof WaitOccurrence w) {
      return Stream.of(w.getUnits());
    }
    if (occ instanceof DeadlockOccurrence) {
      return Stream.empty();
    }
    throw new IllegalArgumentException(
        "unsupported occurrence for expression traversal: %s".formatted(occ));
  }

  private CharSequence generateBody(InteractionFragment f, LifelineContext ctx) {
    // SEMANTICS: fragBody

    // Remember to extend this with any new top-level fragment types added to the metamodel.
    if (f instanceof OccurrenceFragment a) {
      return fragGen.generate(a, ctx);
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
