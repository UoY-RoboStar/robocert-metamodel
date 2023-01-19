/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Expression;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.BlockFragment;
import robostar.robocert.BranchFragment;
import robostar.robocert.DiscreteBound;
import robostar.robocert.ExprGuard;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.InteractionFragment;
import robostar.robocert.InteractionOperand;
import robostar.robocert.LoopFragment;
import robostar.robocert.MessageFragment;
import robostar.robocert.TimeFragment;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.StreamHelper;

/**
 * Finds expressions inside fragments.
 *
 * <p>
 * This utility is useful for working out which variables are referenced within expressions.
 */
public class FragmentExpressionResolver {

  /**
   * Finds all expressions on a fragment, without recursively considering sub-fragments.
   *
   * <p>
   * In the formal semantics, this corresponds to function 'fexprs'.
   *
   * @param f the fragment to search
   * @return a stream (in no particular order) of all expressions defined either directly on the
   * fragment or on the guard expressions of its operands.  The fragments within the operands are
   * not considered.
   */
  public Stream<Expression> expressionsOf(InteractionFragment f) {
    // SEMANTICS: fexprs

    final var direct = directExpressionsOf(f);
    final var inGuards = operandsOf(f).flatMap(this::guardExpressionsOf);

    return Stream.concat(direct, inGuards);
  }

  private Stream<Expression> directExpressionsOf(InteractionFragment f) {
    // SEMANTICS: fdexprs
    return new RoboCertSwitch<Stream<Expression>>() {
      @Override
      public Stream<Expression> defaultCase(EObject object) {
        return Stream.empty();
      }

      @Override
      public Stream<Expression> caseMessageFragment(MessageFragment m) {
        final var args = m.getMessage().getArguments().stream();
        final var argsWithExpressions = StreamHelper.filter(args,
            ExpressionValueSpecification.class);
        return argsWithExpressions.map(ExpressionValueSpecification::getExpr);
      }

      @Override
      public Stream<Expression> caseLoopFragment(LoopFragment l) {
        // The loop may have a bound, whose expressions we'll need to load.
        return Stream.ofNullable(l.getBound()).flatMap(x -> expressionsOf(x));
      }

      @Override
      public Stream<Expression> caseTimeFragment(TimeFragment t) {
        return Stream.of(t.getUnits());
      }
    }.doSwitch(f);
  }

  private Stream<Expression> expressionsOf(DiscreteBound b) {
    // SEMANTICS: bexprs

    return Stream.of(b.getLower(), b.getUpper()).filter(Objects::nonNull);
  }

  private Stream<Expression> guardExpressionsOf(InteractionOperand op) {
    return StreamHelper.filter(Stream.of(op.getGuard()), ExprGuard.class).map(ExprGuard::getExpr);
  }

  private Stream<InteractionOperand> operandsOf(InteractionFragment f) {
    // SEMANTICS: fops

    return new RoboCertSwitch<Stream<InteractionOperand>>() {
      @Override
      public Stream<InteractionOperand> defaultCase(EObject object) {
        return Stream.empty();
      }

      @Override
      public Stream<InteractionOperand> caseBranchFragment(BranchFragment b) {
        return b.getBranches().stream();
      }

      @Override
      public Stream<InteractionOperand> caseBlockFragment(BlockFragment b) {
        return Stream.of(b.getBody());
      }
    }.doSwitch(f);
  }
}
