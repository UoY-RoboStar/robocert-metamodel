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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.Variable;
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
import robostar.robocert.util.factory.ValueSpecificationFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.resolve.EndIndex;
import robostar.robocert.util.resolve.message.IndexedVariableBinding;
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

  private Message evMsg;
  private Message wcMsg;
  private Message opMsg;

  private ResolveContext rctx;
  private Variable wcVar;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();

    resolver = inj.getInstance(MessageResolver.class);
    certFac = inj.getInstance(RoboCertFactory.class);
    msgFac = inj.getInstance(MessageFactory.class);

    final var actFac = inj.getInstance(ActorFactory.class);
    final var chartFac = inj.getInstance(RoboChartBuilderFactory.class);
    final var vsFac = inj.getInstance(ValueSpecificationFactory.class);
    final var tyFac = inj.getInstance(TypeFactory.class);

    final var example = inj.getInstance(MessageResolveExample.class);

    actor = actFac.targetActor("A");

    rctx = new ResolveContext(example.target, List.of(actor));

    final var base = msgFac.async(example.event).fromGate().to(actor);

    evMsg = base.copy().arguments(vsFac.integer(5)).get();

    wcVar = chartFac.var("x", tyFac.primRef("core_nat")).get();
    wcMsg = base.copy().arguments(vsFac.bound(wcVar)).get();

    opMsg = msgFac.sync(msgFac.opTopic(example.op)).from(actor).toGate().get();
  }

  /**
   * Tests that message resolution picks up messages nested within fragments.
   */
  @Test
  void testMessages_nested() {
    final var seq = certFac.createInteraction();

    final var mf1 = msgFac.fragment(evMsg);

    final var mf2 = msgFac.fragment(opMsg);
    final var wrap = certFac.createOptFragment();
    final var wrapBody = certFac.createInteractionOperand();
    wrap.setBody(wrapBody);
    wrapBody.getFragments().add(mf2);

    seq.getFragments().addAll(List.of(mf1, mf2));

    final var msgs = resolver.messages(seq).toList();
    assertThat(msgs, containsInAnyOrder(evMsg, opMsg));
  }

  /**
   * Tests that the actor resolution for messages works properly.
   */
  @Test
  void testActors() {
    assertThat(resolver.actors(evMsg).toList(), containsInAnyOrder(actor));
    assertThat(resolver.actors(opMsg).toList(), containsInAnyOrder(actor));
  }

  /**
   * Tests that the endpoint resolution for messages works properly.
   */
  @Test
  void testEnds() {
    assertThat(resolver.ends(evMsg).toList(), containsInAnyOrder(evMsg.getFrom(), evMsg.getTo()));
    assertThat(resolver.ends(opMsg).toList(), containsInAnyOrder(opMsg.getFrom(), opMsg.getTo()));
  }

  /**
   * Tests resolution on a message over the example integer-event.
   */
  @Test
  void testResolve_Event_Integer() {
    final var rmsg = resolver.resolve(evMsg, rctx);

    assertThat(rmsg.actors(), containsInAnyOrder(actor));
    assertThat(rmsg.message(), is(evMsg));
    assertThat(rmsg.argBindings(), is(empty()));

    // This message goes from gate to target, and so its effective-from is actually 'to'.
    assertThat(rmsg.topic().effectiveFrom(), is(EndIndex.To));
  }

  /**
   * Tests resolution on a message over the example wildcard-event.
   */
  @Test
  void testResolve_Event_Wildcard() {
    final var rmsg = resolver.resolve(wcMsg, rctx);

    assertThat(rmsg.actors(), containsInAnyOrder(actor));
    assertThat(rmsg.message(), is(wcMsg));

    final var binding = new IndexedVariableBinding(0, wcVar);
    assertThat(rmsg.argBindings(), containsInAnyOrder(binding));

    // This message goes from gate to target, and so its effective-from is actually 'to'.
    assertThat(rmsg.topic().effectiveFrom(), is(EndIndex.To));
  }

  /**
   * Tests resolution on a message over the example operation.
   */
  @Test
  void testResolve_Op() {
    final var rmsg = resolver.resolve(opMsg, rctx);

    assertThat(rmsg.actors(), containsInAnyOrder(actor));
    assertThat(rmsg.message(), is(opMsg));
    assertThat(rmsg.argBindings(), is(empty()));

    // This message goes from target to gate, and so its effective-from is 'from'.
    assertThat(rmsg.topic().effectiveFrom(), is(EndIndex.From));
  }
}
