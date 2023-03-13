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

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.InteractionFragment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.factory.robochart.VariableFactory;
import robostar.robocert.util.resolve.FragmentBindingResolver;

/**
 * Tests fragment binding resolution.
 *
 * @author Matt Windsor
 */
class FragmentBindingResolverTest {

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final VariableFactory varFac = VariableFactory.DEFAULT;
  private final TypeFactory tyFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private final ExpressionFactory exprFac = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  private ValueSpecificationFactory vsFac;

  private final FragmentBindingResolver resolver = new FragmentBindingResolver();

  @BeforeEach
  void setUp() {
    vsFac = new ValueSpecificationFactory(exprFac, RoboChartFactory.eINSTANCE, certFac);
  }

  /**
   * Tests {@code expressionsOf} on a {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestExpressionsOf_MessageFragment() {
    final var arg1 = vsFac.integer(42);
    final var arg2 = vsFac.bound(varFac.var("x", tyFac.primRef("int")));

    // red herring
    final var arg3 = vsFac.var(varFac.var("z", tyFac.primRef("real")));

    final var arg4 = vsFac.bound(varFac.var("y", tyFac.primRef("nat")));

    final var from = msgFac.gate();
    final var to = msgFac.occurrence(actFac.targetActor("T"));
    final var op = RoboChartFactory.eINSTANCE.createOperationSig();
    op.setName("op");
    final var topic = msgFac.opTopic(op);
    final var msg = msgFac.message(from, to, topic, arg1, arg2, arg3, arg4);

    final var f = certFac.createMessageFragment();
    f.setMessage(msg);

    assertThat(f, hasBindings(arg2.getDestination(), arg4.getDestination()));
  }

  private Matcher<InteractionFragment> hasBindings(Variable... xs) {
    return new CustomTypeSafeMatcher<>("has the given bindings") {
      @Override
      protected boolean matchesSafely(InteractionFragment f) {
        return contains(xs).matches(resolver.bindingsOf(f).toList());
      }

      @Override
      protected void describeMismatchSafely(InteractionFragment f,
          Description mismatchDescription) {
        contains(xs).describeMismatch(resolver.bindingsOf(f).toList(), mismatchDescription);
      }
    };
  }
}
