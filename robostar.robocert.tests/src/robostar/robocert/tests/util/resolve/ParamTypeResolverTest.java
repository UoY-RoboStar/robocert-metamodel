/*
 * Copyright (c) 2021-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import robostar.robocert.EventTopic;
import robostar.robocert.MessageTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.robochart.EventFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.resolve.ParamTypeResolver;

/**
 * Tests topic parameter type resolution.
 *
 * @author Matt Windsor
 */
class ParamTypeResolverTest {

  private RoboChartBuilderFactory chartFac;
  private final TypeFactory typeFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final ParamTypeResolver paramTypeRes = new ParamTypeResolver();

  private Event event;
  private EventTopic eventTopic;

  private Parameter[] opParams;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();
    chartFac = inj.getInstance(RoboChartBuilderFactory.class);
    msgFac = inj.getInstance(MessageFactory.class);

    event = inj.getInstance(EventFactory.class).event("foo");
    eventTopic = msgFac.eventTopic(event);

    opParams = new Parameter[3];
    opParams[0] = chartFac.parameter("a", typeFac.primRef("int"));
    opParams[1] = chartFac.parameter("b", typeFac.primRef("real"));
    opParams[2] = chartFac.parameter("c", typeFac.primRef("boolean"));
  }


  /**
   * Tests that {@code ParamTypeResolver} on an event topic with no type returns an empty type
   * list.
   */
  @Test
  void testEmptyEvent() {
    // By default, the event is set up to not have a type.
    assertThat(paramTypes(eventTopic), is(empty()));
  }

  /**
   * Tests that {@code ParamTypeResolver} on a topic with a type returns a singleton type list.
   */
  @Test
  void testGetParamTypes_nonEmpty() {
    event.setType(typeFac.primRef("int"));
    assertThat(paramTypes(eventTopic), contains(event.getType()));
  }

  //
  // Operations
  //


  /**
   * Tests that {@code getParamTypes} on a topic with no parameters returns an empty type list.
   */
  @Test
  void testGetParamTypes_empty() {
    var op = chartFac.operationSig("foo").get();
    assertThat(paramTypes(msgFac.opTopic(op)), is(empty()));
  }

  /**
   * Tests that {@code getParamTypes} on a topic with some parameters returns the expected list.
   *
   * @param info information about the test repetition.
   */
  @RepeatedTest(3)
  void testGetParamTypes(RepetitionInfo info) {
    var opBuilder = chartFac.operationSig("foo");

    final var numParams = info.getCurrentRepetition();
    // This convoluted explicit type is required to get the right resolution below!
    final List<Matcher<? super Type>> matchers = new ArrayList<>(numParams);
    for (int i = 0; i < numParams; i++) {
      opBuilder = opBuilder.parameters(opParams[i]);
      matchers.add(is(opParams[i].getType()));
    }

    var op = opBuilder.get();
    assertThat(paramTypes(msgFac.opTopic(op)), contains(matchers));
  }

  //
  // Utility functions
  //

  private List<Type> paramTypes(MessageTopic t) {
    return paramTypeRes.resolve(t).toList();
  }
}
