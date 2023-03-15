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
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.*;
import robostar.robocert.tests.examples.ForagingExample;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.SetFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.resolve.*;
import robostar.robocert.util.resolve.node.ActorNodeResolver;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;
import robostar.robocert.util.resolve.node.WorldNodeResolver;

import java.util.Set;
import java.util.stream.Collectors;
import robostar.robocert.util.resolve.result.ResolvedEvent;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests that the {@link EventResolverImpl} seems to be resolving things correctly on
 * {@link ForagingExample}.
 *
 * @author Matt Windsor
 */
class EventResolverTest {

  private EventResolver resolver;
  private final ForagingExample example = new ForagingExample(RoboChartFactory.eINSTANCE);
  private final RoboCertFactory certFactory = RoboCertFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = MessageFactory.DEFAULT;
  private final SetFactory setFac = SetFactory.DEFAULT;
  private final TargetFactory targetFactory = new TargetFactory(RoboCertFactory.eINSTANCE);

  private Gate world;
  private MessageOccurrence target;
  private MessageEndWrapper wrapper;
  private Actor actor;

  @BeforeEach
  void setUp() {
    // TODO(@MattWindsor91): fix dependency injection here.
    final var groupFinder = new GroupFinder();
    final var tgtRes = new TargetNodeResolver();
    final var defRes = new DefinitionResolver();
    final var ctrlRes = new ControllerResolver();
    final var modRes = new ModuleResolver(defRes);
    final var stmRes = new StateMachineResolver(ctrlRes);
    final var aNodeRes = new ActorNodeResolver(tgtRes, groupFinder);
    final var wNodeRes = new WorldNodeResolver(modRes, ctrlRes, stmRes, aNodeRes, groupFinder);
    final var endRes = new MessageEndNodeResolver(aNodeRes, wNodeRes);
    final var outRes = new OutboundConnectionResolver(modRes, ctrlRes, stmRes, defRes);
    resolver = new EventResolverImpl(endRes, tgtRes, modRes, ctrlRes, stmRes, groupFinder, outRes);

    world = msgFac.gate();
    actor = actFac.targetActor("T");
    target = msgFac.occurrence(actor);

    wrapper = new MessageEndWrapper(certFactory, msgFac);
  }


  /**
   * Tests resolving connections on a module in the example.
   */
  @Test
  void testResolve_module() {
    // TODO(@MattWindsor91): clean this up and check directions.

    final var mod = targetFactory.module(example.foraging);
    final var grp = wrapper.wrap(mod, world, target);

    final var events1 = resolve(example.platformObstacle, example.obstacleAvoidanceObstacle, world,
        target, grp);
    final var conns1 = events1.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns1, hasItems(example.obstaclePlatformToObstacleAvoidance));

    // This connection is not bidirectional, so this should be empty.
    final var events2 = resolve(example.obstacleAvoidanceObstacle, example.platformObstacle, target,
        world, grp);
    assertThat(events2, is(empty()));

    // Inferring an eto.
    final var events3 = resolve(example.platformObstacle, null, world, target, grp);
    final var conns3 = events3.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns3, hasItems(example.obstaclePlatformToObstacleAvoidance));
  }

  /**
   * Tests resolving connections on a state machine in the example.
   */
  @Test
  void testResolve_stateMachine() {
    // TODO(@MattWindsor91): clean this up and check directions.

    final var stm = targetFactory.stateMachine(example.avoid);
    final var grp = wrapper.wrap(stm, world, target);

    final var events1 = resolve(example.obstacleAvoidanceObstacle, example.avoidObstacle, world,
        target, grp);
    final var conns1 = events1.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns1, hasItems(example.obstacleObstacleAvoidanceToAvoid));

    // This connection is not bidirectional, so this should be empty.
    final var events2 = resolve(example.avoidObstacle, example.obstacleAvoidanceObstacle, target,
        world, grp);
    assertThat(events2, is(empty()));

    // Inferring an eto.
    final var events3 = resolve(example.obstacleAvoidanceObstacle, null, world, target, grp);
    final var conns3 = events3.stream().map(ResolvedEvent::connection)
        .collect(Collectors.toUnmodifiableSet());
    assertThat(conns3, hasItems(example.obstacleObstacleAvoidanceToAvoid));
  }

  private Set<ResolvedEvent> resolve(Event efrom, Event eto, MessageEnd from, MessageEnd to, SpecificationGroup grp) {
    final var topic = msgFac.eventTopic(efrom, eto);
    final var msg = msgFac.message(from, to, topic);

    // Necessary to get the endpoints into a SpecificationGroup
    grp.getMessageSets().add(setFac.named("set", setFac.extensional(msg)));

    final var query = new EventResolverQuery(msg, topic, List.of(actor));
    return resolver.resolve(query).collect(Collectors.toUnmodifiableSet());
  }
}
