/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve;

import circus.robocalc.robochart.Event;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.*;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.tests.examples.ForagingExample;
import robostar.robocert.tests.examples.MessageResolveExample;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.resolve.message.EventResolver;
import robostar.robocert.util.resolve.message.EventResolverImpl;
import robostar.robocert.util.resolve.message.EventResolverQuery;
import robostar.robocert.util.resolve.node.ResolveContext;

import java.util.Set;
import java.util.stream.Collectors;
import robostar.robocert.util.resolve.result.ResolvedEvent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests that the {@link EventResolverImpl} seems to be resolving things correctly on various
 * examples.
 *
 * @author Matt Windsor
 */
class EventResolverTest {

  private EventResolver resolver;

  private ForagingExample foragingExample;
  private MessageResolveExample msgResolveExample;

  private MessageFactory msgFac;
  private TargetFactory tgtFac;

  private Actor actor;
  private MessageOccurrence target;
  private Gate gate;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();

    resolver = inj.getInstance(EventResolver.class);

    foragingExample = inj.getInstance(ForagingExample.class);
    msgResolveExample = inj.getInstance(MessageResolveExample.class);

    msgFac = inj.getInstance(MessageFactory.class);
    tgtFac = inj.getInstance(TargetFactory.class);

    actor = inj.getInstance(ActorFactory.class).targetActor("T");
    target = msgFac.occurrence(actor);
    gate = msgFac.gate();
  }

  /**
   * Tests resolving connections on a toy module.
   */
  @Test
  void testResolve_MessageResolve_Module() {
    final var events = resolve(msgResolveExample.event, null, gate, target,
        msgResolveExample.target);

    final var conns = events.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns, hasItems(msgResolveExample.conn));
  }

  /**
   * Tests resolving connections on a module in the foraging example.
   */
  @Test
  void testResolve_Foraging_Module() {
    // TODO(@MattWindsor91): clean this up and check directions.

    final var mod = tgtFac.module(foragingExample.foraging);

    final var events1 = resolve(foragingExample.platformObstacle,
        foragingExample.obstacleAvoidanceObstacle, gate, target, mod);
    final var conns1 = events1.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns1, hasItems(foragingExample.obstaclePlatformToObstacleAvoidance));

    // This connection is not bidirectional, so this should be empty.
    final var events2 = resolve(foragingExample.obstacleAvoidanceObstacle,
        foragingExample.platformObstacle, target, gate, mod);
    assertThat(events2, is(empty()));

    // Inferring an eto.
    final var events3 = resolve(foragingExample.platformObstacle, null, gate, target, mod);
    final var conns3 = events3.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns3, hasItems(foragingExample.obstaclePlatformToObstacleAvoidance));
  }

  /**
   * Tests resolving connections on a state machine in the example.
   */
  @Test
  void testResolve_Foraging_StateMachine() {
    // TODO(@MattWindsor91): clean this up and check directions.

    final var stm = tgtFac.stateMachine(foragingExample.avoid);

    final var events1 = resolve(foragingExample.obstacleAvoidanceObstacle,
        foragingExample.avoidObstacle, gate, target, stm);
    final var conns1 = events1.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns1, hasItems(foragingExample.obstacleObstacleAvoidanceToAvoid));

    // This connection is not bidirectional, so this should be empty.
    final var events2 = resolve(foragingExample.avoidObstacle,
        foragingExample.obstacleAvoidanceObstacle, target, gate, stm);
    assertThat(events2, is(empty()));

    // Inferring an eto.
    final var events3 = resolve(foragingExample.obstacleAvoidanceObstacle, null, gate, target, stm);
    final var conns3 = events3.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns3, hasItems(foragingExample.obstacleObstacleAvoidanceToAvoid));
  }

  private Set<ResolvedEvent> resolve(Event efrom, Event eto, MessageEnd from, MessageEnd to,
      Target target) {
    final var topic = msgFac.eventTopic(efrom, eto);
    final var msg = msgFac.message(from, to, topic);

    final var query = new EventResolverQuery(msg, topic,
        new ResolveContext(target, List.of(actor)));
    return resolver.resolve(query).collect(Collectors.toUnmodifiableSet());
  }
}
