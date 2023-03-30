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
import static org.hamcrest.Matchers.is;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Actor;
import robostar.robocert.Message;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.tests.examples.MessageResolveExample;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.resolve.EndIndex;
import robostar.robocert.util.resolve.message.MessageResolver;
import robostar.robocert.util.resolve.node.ResolveContext;

/**
 * Tests the various message resolution methods.
 *
 * @author Matt Windsor
 */
class MessageResolverTest {

  private MessageFactory msgFac;
  private RoboCertFactory certFac;
  private MessageResolver resolver;

  private Actor actor;

  private Message msg1;
  private Message msg2;

  private ResolveContext rctx;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();

    resolver = inj.getInstance(MessageResolver.class);
    certFac = inj.getInstance(RoboCertFactory.class);
    msgFac = inj.getInstance(MessageFactory.class);

    final var example = inj.getInstance(MessageResolveExample.class);

    final var actFac = inj.getInstance(ActorFactory.class);
    actor = actFac.targetActor("A");

    rctx = new ResolveContext(example.target, List.of(actor));

    msg1 = msgFac.message(msgFac.gate(), msgFac.occurrence(actor),
        msgFac.eventTopic(example.event));
    msg2 = msgFac.message(msgFac.occurrence(actor), msgFac.gate(), msgFac.opTopic(example.op));
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
    assertThat(resolver.actors(msg1).toList(), containsInAnyOrder(actor));
    assertThat(resolver.actors(msg2).toList(), containsInAnyOrder(actor));
  }

  /**
   * Tests that the endpoint resolution for messages works properly.
   */
  @Test
  void testEnds() {
    assertThat(resolver.ends(msg1).toList(), containsInAnyOrder(msg1.getFrom(), msg1.getTo()));
    assertThat(resolver.ends(msg2).toList(), containsInAnyOrder(msg2.getFrom(), msg2.getTo()));
  }

  /**
   * Tests resolution on a message over the example event.
   */
  @Test
  void testResolve_Event() {
    final var rmsg = resolver.resolve(msg1, rctx);

    assertThat(rmsg.actors(), containsInAnyOrder(actor));
    assertThat(rmsg.message(), is(msg1));

    // This message goes from gate to target, and so its effective-from is actually 'to'.
    assertThat(rmsg.topic().effectiveFrom(), is(EndIndex.To));
  }

  /**
   * Tests resolution on a message over the example operation.
   */
  @Test
  void testResolve_Op() {
    final var rmsg = resolver.resolve(msg2, rctx);

    assertThat(rmsg.actors(), containsInAnyOrder(actor));
    assertThat(rmsg.message(), is(msg2));

    // This message goes from target to gate, and so its effective-from is 'from'.
    assertThat(rmsg.topic().effectiveFrom(), is(EndIndex.From));
  }
}
