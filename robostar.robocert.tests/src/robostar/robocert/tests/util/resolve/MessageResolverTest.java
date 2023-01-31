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
import static org.hamcrest.Matchers.containsInAnyOrder;

import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Actor;
import robostar.robocert.Message;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.resolve.MessageResolver;

/**
 * Tests the various message resolution methods.
 *
 * @author Matt Windsor
 */
class MessageResolverTest {

  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final RoboChartFactory chartFac = RoboChartFactory.eINSTANCE;

  private Actor act1;
  private Actor act2;

  private Message msg1;
  private Message msg2;

  private final MessageResolver resolver = new MessageResolver();

  @BeforeEach
  void setUp() {
    act1 = actFac.targetActor("A1");
    act2 = actFac.targetActor("A2");

    var evt1 = chartFac.createEvent();
    evt1.setName("evt1");

    var op1 = chartFac.createOperationSig();
    op1.setName("op1");

    msg1 = msgFac.message(msgFac.actor(act1), msgFac.gate(), msgFac.eventTopic(evt1));
    msg2 = msgFac.message(msgFac.gate(), msgFac.actor(act2), msgFac.opTopic(op1));
  }

  /**
   * Tests that message resolution picks up messages nested within fragments.
   */
  @Test
  void testMessages_nested() {
    final var seq = certFac.createInteraction();

    final var mf1 = msgFac.fragment(msg1);

    final var mf2 = msgFac.fragment(msg2);
    final var wrap = certFac.createOptFragment();
    final var wrapBody = certFac.createInteractionOperand();
    wrap.setBody(wrapBody);
    wrapBody.getFragments().add(mf2);

    seq.getFragments().addAll(List.of(mf1, mf2));

    final var msgs = resolver.messages(seq).toList();
    assertThat(msgs, containsInAnyOrder(msg1, msg2));
  }

  /**
   * Tests that the actor resolution for messages works properly.
   */
  @Test
  void testActors() {
    assertThat(resolver.actors(msg1).toList(), containsInAnyOrder(act1));
    assertThat(resolver.actors(msg2).toList(), containsInAnyOrder(act2));
  }

  /**
   * Tests that the endpoint resolution for messages works properly.
   */
  @Test
  void testMessageEnd() {
    assertThat(resolver.ends(msg1).toList(), containsInAnyOrder(msg1.getFrom(), msg1.getTo()));
    assertThat(resolver.ends(msg2).toList(), containsInAnyOrder(msg2.getFrom(), msg2.getTo()));
  }
}
