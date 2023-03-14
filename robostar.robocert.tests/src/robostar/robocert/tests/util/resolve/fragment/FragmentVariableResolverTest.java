/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve.fragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import circus.robocalc.robochart.RoboChartFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.factory.robochart.VariableFactory;
import robostar.robocert.util.resolve.ExpressionVariableReferenceResolver;
import robostar.robocert.util.resolve.fragment.FragmentBindingResolver;
import robostar.robocert.util.resolve.fragment.FragmentExpressionResolver;
import robostar.robocert.util.resolve.fragment.FragmentVariableResolver;

/**
 * Tests fragment variable resolution.
 *
 * @author Matt Windsor
 */
class FragmentVariableResolverTest {

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final VariableFactory varFac = VariableFactory.DEFAULT;
  private final TypeFactory tyFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private final ExpressionFactory exprFac = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  private ValueSpecificationFactory vsFac;

  private final FragmentVariableResolver resolver = new FragmentVariableResolver(
      new ExpressionVariableReferenceResolver(), new FragmentBindingResolver(),
      new FragmentExpressionResolver());

  @BeforeEach
  void setUp() {
    vsFac = new ValueSpecificationFactory(exprFac, RoboChartFactory.eINSTANCE, certFac);
  }

  /**
   * Tests {@code bindingsOf} on a {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestBindingsOf_MessageFragment() {
    // TODO: possibly DRY up this example with that in FragmentBindingResolverTest et al
    final var arg1 = vsFac.integer(42);
    final var arg2 = vsFac.bound(varFac.var("x", tyFac.primRef("int")));
    final var in = varFac.var("z", tyFac.primRef("real"));
    final var arg3 = vsFac.var(in);
    final var arg4 = vsFac.bound(varFac.var("y", tyFac.primRef("nat")));

    final var from = msgFac.gate();
    final var to = msgFac.occurrence(actFac.targetActor("T"));
    final var op = RoboChartFactory.eINSTANCE.createOperationSig();
    op.setName("op");
    final var topic = msgFac.opTopic(op);
    final var msg = msgFac.message(from, to, topic, arg1, arg2, arg3, arg4);

    final var f = certFac.createMessageFragment();
    f.setMessage(msg);

    final var vars = resolver.variablesOf(f);
    assertThat(vars.inputs(), containsInAnyOrder(in));
    assertThat(vars.outputs(), containsInAnyOrder(arg2.getDestination(), arg4.getDestination()));
  }
}
