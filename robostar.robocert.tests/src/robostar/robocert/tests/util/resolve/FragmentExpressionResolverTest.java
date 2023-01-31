/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.InteractionFragment;
import robostar.robocert.InteractionOperand;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.WaitFragment;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.util.resolve.FragmentExpressionResolver;

/**
 * Tests fragment expression resolution.
 *
 * @author Matt Windsor
 */
class FragmentExpressionResolverTest {

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final ExpressionFactory exprFac = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  private ValueSpecificationFactory vsFac;

  private final FragmentExpressionResolver resolver = new FragmentExpressionResolver();

  /**
   * Expression that we can use to check resolution of units expressions.
   */
  private Expression units;

  /**
   * Expression that must not be resolved when we consider the first operand of a combined
   * fragment.
   */
  private Expression redHerring;
  /**
   * Expression used in the first guard of a combined fragment.
   */
  private Expression guardCond;

  /**
   * Operand used to test block fragments or the first branch in branch fragments.
   */
  private InteractionOperand operand;

  @BeforeEach
  void setUp() {
    vsFac = new ValueSpecificationFactory(exprFac, RoboChartFactory.eINSTANCE, certFac);

    units = exprFac.integer(42);
    redHerring = exprFac.integer(0xBAADF00D);
    guardCond = exprFac.bool(true);

    // Make sure that we *don't* pick up the red herring, but *do* pick up the guard
    operand = certFac.createInteractionOperand();
    operand.getFragments().add(wait(redHerring));

    final var guard = certFac.createExprGuard();
    guard.setExpr(guardCond);
    operand.setGuard(guard);
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.DeadlineFragment}.
   */
  @Test
  void TestExpressionsOf_DeadlineFragment() {

    final var act = actFac.targetActor("tgt");
    final var df = certFac.createDeadlineFragment();
    df.setActor(act);
    df.setUnits(units);
    df.setBody(operand);

    assertThat(df, hasExpressions(units, guardCond));
    assertThat(df, not(hasExpressions(redHerring)));
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.WaitFragment}.
   */
  @Test
  void TestExpressionsOf_WaitFragment() {
    final var units = exprFac.integer(42);
    assertThat(wait(units), hasExpressions(units));
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestExpressionsOf_MessageFragment() {
    final var arg1 = vsFac.integer(42);
    final var arg2 = vsFac.integer(64);
    final var arg3 = vsFac.integer(0xF00DF00D);

    final var from = msgFac.gate();
    final var to = msgFac.actor(actFac.targetActor("T"));
    final var op = RoboChartFactory.eINSTANCE.createOperationSig();
    op.setName("op");
    final var topic = msgFac.opTopic(op);
    final var msg = msgFac.message(from, to, topic, arg1, arg2, arg3);

    final var f = certFac.createMessageFragment();
    f.setMessage(msg);

    assertThat(f, hasExpressions(arg1.getExpr(), arg2.getExpr(), arg3.getExpr()));
  }

  /**
   * Tests {@code expressionsOf} on an {@link robostar.robocert.AltFragment}.
   */
  @Test
  void TestExpressionsOf_AltFragment() {
    // Set up a second branch in the same way as we set up a first one in setUp()
    final var redHerring2 = exprFac.integer(0x00BADBAD);
    final var guardCond2 = exprFac.bool(false);
    final var operand2 = certFac.createInteractionOperand();
    operand2.getFragments().add(wait(redHerring2));
    final var guard2 = certFac.createExprGuard();
    guard2.setExpr(guardCond2);
    operand2.setGuard(guard2);

    final var alt = certFac.createAltFragment();
    alt.getBranches().addAll(List.of(operand, operand2));

    assertThat(alt, hasExpressions(guardCond, guardCond2));
    assertThat(alt, not(hasExpressions(redHerring, redHerring2)));
  }

  private WaitFragment wait(Expression units) {
    final var act = actFac.targetActor("tgt");
    final var wf = certFac.createWaitFragment();
    wf.setActor(act);
    wf.setUnits(units);
    return wf;
  }

  private Matcher<InteractionFragment> hasExpressions(Expression... xs) {
    return new CustomTypeSafeMatcher<>("has the given expressions") {
      @Override
      protected boolean matchesSafely(InteractionFragment f) {
        return contains(xs).matches(resolver.expressionsOf(f).toList());
      }

      @Override
      protected void describeMismatchSafely(InteractionFragment f,
          Description mismatchDescription) {
        contains(xs).describeMismatch(resolver.expressionsOf(f).toList(), mismatchDescription);
      }
    };
  }
}
