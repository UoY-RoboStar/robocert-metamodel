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
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.util.resolve.FragmentExpressionResolver;

/**
 * Tests fragment expression resolution.
 *
 * @author Matt Windsor
 */
class FragmentExpressionResolverTest {

  private final RoboCertFactory rc = RoboCertFactory.eINSTANCE;
  private final MessageFactory msgFactory = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final ExpressionFactory exprFactory = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  private ValueSpecificationFactory vsFactory;

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
    vsFactory = new ValueSpecificationFactory(exprFactory, RoboChartFactory.eINSTANCE, rc);

    units = exprFactory.integer(42);
    redHerring = exprFactory.integer(0xBAADF00D);
    guardCond = exprFactory.bool(true);

    // Make sure that we *don't* pick up the red herring, but *do* pick up the guard
    operand = rc.createInteractionOperand();
    operand.getFragments().add(wait(redHerring));

    final var guard = rc.createExprGuard();
    guard.setExpr(guardCond);
    operand.setGuard(guard);
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.DeadlineFragment}.
   */
  @Test
  void TestExpressionsOf_DeadlineFragment() {

    final var act = msgFactory.targetActor("tgt");
    final var df = rc.createDeadlineFragment();
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
    final var units = exprFactory.integer(42);
    assertThat(wait(units), hasExpressions(units));
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestExpressionsOf_MessageFragment() {
    final var arg1 = vsFactory.integer(42);
    final var arg2 = vsFactory.integer(64);
    final var arg3 = vsFactory.integer(0xF00DF00D);

    final var from = msgFactory.world();
    final var to = msgFactory.actor(msgFactory.targetActor("T"));
    final var op = RoboChartFactory.eINSTANCE.createOperationSig();
    op.setName("op");
    final var topic = msgFactory.opTopic(op);
    final var msg = msgFactory.spec(from, to, topic, arg1, arg2, arg3);

    final var f = rc.createMessageFragment();
    f.setMessage(msg);

    assertThat(f, hasExpressions(arg1.getExpr(), arg2.getExpr(), arg3.getExpr()));
  }

  /**
   * Tests {@code expressionsOf} on an {@link robostar.robocert.AltFragment}.
   */
  @Test
  void TestExpressionsOf_AltFragment() {
    // Set up a second branch in the same way as we set up a first one in setUp()
    final var redHerring2 = exprFactory.integer(0x00BADBAD);
    final var guardCond2 = exprFactory.bool(false);
    final var operand2 = rc.createInteractionOperand();
    operand2.getFragments().add(wait(redHerring2));
    final var guard2 = rc.createExprGuard();
    guard2.setExpr(guardCond2);
    operand2.setGuard(guard2);

    final var alt = rc.createAltFragment();
    alt.getBranches().addAll(List.of(operand, operand2));

    assertThat(alt, hasExpressions(guardCond, guardCond2));
    assertThat(alt, not(hasExpressions(redHerring, redHerring2)));
  }

  private WaitFragment wait(Expression units) {
    final var act = msgFactory.targetActor("tgt");
    final var wf = rc.createWaitFragment();
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
