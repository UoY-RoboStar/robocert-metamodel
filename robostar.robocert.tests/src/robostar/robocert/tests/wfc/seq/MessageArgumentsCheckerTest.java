/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.wfc.seq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.IntegerExp;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Actor;
import robostar.robocert.EventTopic;
import robostar.robocert.Message;
import robostar.robocert.ValueSpecification;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.EventFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.resolve.ParamTypeResolver;
import robostar.robocert.wfc.seq.MessageArgumentsChecker;

/**
 * Tests {@link MessageArgumentsChecker}.
 *
 * @author Matt Windsor
 */
class MessageArgumentsCheckerTest {

  private MessageArgumentsChecker checker;
  private final ParamTypeResolver paramTypeRes = new ParamTypeResolver();
  private final TypeFactory typeFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final EventFactory eventFac = new EventFactory(RoboChartFactory.eINSTANCE);
  private final MessageFactory msgFac = MessageFactory.DEFAULT;
  private final ValueSpecificationFactory vsFac = ValueSpecificationFactory.DEFAULT;

  private EventTopic untypedEvent;
  private EventTopic typedEvent;

  private Actor actor;

  private Type nat;
  private Type integer;

  @BeforeEach
  void setUp() {
    nat = typeFac.primRef("nat");
    integer = typeFac.primRef("int");

    checker = new MessageArgumentsChecker(this::checkType, paramTypeRes);

    untypedEvent = msgFac.eventTopic(eventFac.event("untyped"));
    typedEvent = msgFac.eventTopic(eventFac.event("typed", nat));

    actor = actFac.targetActor("T");
  }

  private boolean checkType(Expression expr, Type wanted) {
    if (expr instanceof IntegerExp i) {
      final var got = i.getValue() < 0 ? integer : nat;
      return wanted.equals(got);
    }

    // no other types modelled
    return false;
  }

  /**
   * Tests that an empty message is both SMA1 and SMA2 on the untyped event.
   */
  @Test
  void testCheck_Empty_Untyped_SMA1_SMA2() {
    final var res = checker.check(message(untypedEvent));
    assertTrue(res.isSMA1(), "message should be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that an empty message is SMA2, but not SMA1, on the typed event.
   */
  @Test
  void testCheck_Empty_Typed_SMA2() {
    final var res = checker.check(message(typedEvent));
    assertFalse(res.isSMA1(), "message should not be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that a message with a wildcard is SMA2, but not SMA1, on the untyped event.
   */
  @Test
  void testCheck_Wildcard_Untyped_SMA1_SMA2() {
    final var res = checker.check(message(untypedEvent, vsFac.wildcard()));
    assertFalse(res.isSMA1(), "message should not be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that a message with a wildcard is both SMA1 and SMA2 on the typed event.
   */
  @Test
  void testCheck_Wildcard_Typed_SMA1_SMA2() {
    final var res = checker.check(message(typedEvent, vsFac.wildcard()));
    assertTrue(res.isSMA1(), "message should be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }


  /**
   * Tests that a message with a correctly typed expression is SMA2, but not SMA1, on the untyped
   * event.
   */
  @Test
  void testCheck_Nat_Untyped_SMA1_SMA2() {
    final var res = checker.check(message(untypedEvent, vsFac.integer(2)));
    assertFalse(res.isSMA1(), "message should not be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that a message with a correctly typed expression is both SMA1 and SMA2 on the typed
   * event.
   */
  @Test
  void testCheck_Nat_Typed_SMA1_SMA2() {
    final var res = checker.check(message(typedEvent, vsFac.integer(2)));
    assertTrue(res.isSMA1(), "message should be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that a message with a wrongly typed expression is SMA2, but not SMA1, on the untyped
   * event.
   */
  @Test
  void testCheck_Int_Untyped_SMA2() {
    final var res = checker.check(message(untypedEvent, vsFac.integer(-2)));
    assertFalse(res.isSMA1(), "message should not be SMA1");
    assertTrue(res.isSMA2(), "message should be SMA2");
  }

  /**
   * Tests that a message with a wrongly typed expression is SMA1, but not SMA2, on the typed
   * event.
   */
  @Test
  void testCheck_Int_Typed_SMA1() {
    final var res = checker.check(message(typedEvent, vsFac.integer(-2)));
    assertTrue(res.isSMA1(), "message should be SMA1");
    assertFalse(res.isSMA2(), "message should not be SMA2");
  }

  private Message message(EventTopic event, ValueSpecification... args) {
    return msgFac.message(msgFac.gate(), msgFac.occurrence(actor), event, args);
  }
}
