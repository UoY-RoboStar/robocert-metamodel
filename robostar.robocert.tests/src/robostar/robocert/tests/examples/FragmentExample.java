/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.examples;

import circus.robocalc.robochart.Variable;
import javax.inject.Inject;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.MessageFragment;
import robostar.robocert.WildcardValueSpecification;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.factory.robochart.VariableFactory;

/**
 * A toy example to demonstrate message fragments.
 */
public class FragmentExample {

  /**
   * The example fragment.
   */
  public final MessageFragment fragment;
  /**
   * The first example argument.
   */
  public final ExpressionValueSpecification arg1;
  /**
   * The second example argument.
   */
  public final WildcardValueSpecification arg2;
  /**
   * The variable used in the third example argument.
   */
  public final Variable var3;
  /**
   * The third example argument.
   */
  public final ExpressionValueSpecification arg3;
  /**
   * The fourth example argument.
   */
  public final WildcardValueSpecification arg4;

  @Inject
  public FragmentExample(ActorFactory actFac, MessageFactory msgFac,
      RoboChartBuilderFactory chartFac, TypeFactory tyFac, ValueSpecificationFactory vsFac,
      VariableFactory varFac) {
    arg1 = vsFac.integer(42);
    arg2 = vsFac.bound(varFac.var("x", tyFac.primRef("int")));

    var3 = varFac.var("z", tyFac.primRef("real"));
    arg3 = vsFac.var(var3);

    arg4 = vsFac.bound(varFac.var("y", tyFac.primRef("nat")));

    final var from = msgFac.gate();
    final var to = msgFac.occurrence(actFac.targetActor("T"));

    final var topic = msgFac.opTopic(chartFac.operationSig("op").get());
    final var msg = msgFac.message(from, to, topic, arg1, arg2, arg3, arg4);

    fragment = msgFac.fragment(msg);
  }
}
