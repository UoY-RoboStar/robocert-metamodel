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

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.RoboChartFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.*;
import robostar.robocert.tests.examples.ForagingExample;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.util.resolve.*;
import robostar.robocert.util.resolve.node.ActorNodeResolver;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;
import robostar.robocert.util.resolve.node.WorldNodeResolver;

/**
 * Tests that the {@link ActorNodeResolver} seems to be resolving things correctly on
 * {@link ForagingExample}.
 *
 * @author Matt Windsor
 */
class MessageEndNodeResolverTest {


  private MessageEndNodeResolver resolver;
  private final ForagingExample example = new ForagingExample(RoboChartFactory.eINSTANCE);
  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final TargetFactory tgtFac = new TargetFactory(RoboCertFactory.eINSTANCE);
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageEndWrapper wrapper = MessageEndWrapper.DEFAULT;

  private Gate world;
  private MessageOccurrence target;
  private List<Lifeline> lines;

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
    resolver = new MessageEndNodeResolver(aNodeRes, wNodeRes);

    world = msgFac.gate();
    final var actor = actFac.targetActor("T");
    target = msgFac.actor(actor);
    lines = List.of(actFac.lifeline(actor));
  }

  /**
   * Tests resolving connection nodes on a controller in the example.
   */
  @Test
  void testResolve_controller() {
    final var stm = tgtFac.controller(example.obstacleAvoidance);
    wrapper.wrap(stm, world, target);

    final var worldNodes = resolve(world);
    assertThat(worldNodes, hasItems(example.platform));

    final var targetNodes = resolve(target);
    assertThat(targetNodes, hasItems(example.obstacleAvoidance));
  }

  /**
   * Tests resolving connection nodes on a state machine in the example.
   */
  @Test
  void testResolve_stateMachine() {
    final var stm = tgtFac.stateMachine(example.avoid);
    wrapper.wrap(stm, world, target);

    // Since 2023-01-23, the world of a state machine is just the controller and siblings.
    final var worldNodes = resolve(world);
    assertThat(worldNodes, hasItems(example.obstacleAvoidance));

    final var targetNodes = resolve(target);
    assertThat(targetNodes, hasItems(example.avoid));
  }

  private Set<ConnectionNode> resolve(MessageEnd a) {
    return resolver.resolve(a, lines).collect(Collectors.toUnmodifiableSet());
  }
}
