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

import com.google.inject.Guice;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.*;
import robostar.robocert.tests.examples.ForagingExample;
import robostar.robocert.util.RoboCertBaseModule;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.resolve.node.ActorNodeResolver;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.node.ResolveContext;

/**
 * Tests that the {@link ActorNodeResolver} seems to be resolving things correctly on
 * {@link ForagingExample}.
 *
 * @author Matt Windsor
 */
class MessageEndNodeResolverTest {


  private MessageEndNodeResolver resolver;
  private ForagingExample example;
  private TargetFactory tgtFac;

  private Gate world;
  private MessageOccurrence target;
  private Actor actor;

  @BeforeEach
  void setUp() {
    final var inj = Guice.createInjector(new RoboCertBaseModule());

    tgtFac = inj.getInstance(TargetFactory.class);
    example = inj.getInstance(ForagingExample.class);
    resolver = inj.getInstance(MessageEndNodeResolver.class);

    actor = inj.getInstance(ActorFactory.class).targetActor("T");

    final var msgFac = inj.getInstance(MessageFactory.class);
    world = msgFac.gate();
    target = msgFac.occurrence(actor);
  }

  /**
   * Tests resolving connection nodes on a controller in the example.
   */
  @Test
  void testResolve_controller() {
    final var stm = tgtFac.controller(example.obstacleAvoidance);

    final var worldNodes = resolve(world, stm);
    assertThat(worldNodes, hasItems(example.platform));

    final var targetNodes = resolve(target, stm);
    assertThat(targetNodes, hasItems(example.obstacleAvoidance));
  }

  /**
   * Tests resolving connection nodes on a state machine in the example.
   */
  @Test
  void testResolve_stateMachine() {
    final var stm = tgtFac.stateMachine(example.avoid);

    // Since 2023-01-23, the world of a state machine is just the controller and siblings.
    final var worldNodes = resolve(world, stm);
    assertThat(worldNodes, hasItems(example.obstacleAvoidance));

    final var targetNodes = resolve(target, stm);
    assertThat(targetNodes, hasItems(example.avoid));
  }

  private Set<ConnectionNode> resolve(MessageEnd a, Target t) {
    final var ctx = new ResolveContext(t, List.of(actor));
    return resolver.resolve(a, ctx).collect(Collectors.toUnmodifiableSet());
  }
}
