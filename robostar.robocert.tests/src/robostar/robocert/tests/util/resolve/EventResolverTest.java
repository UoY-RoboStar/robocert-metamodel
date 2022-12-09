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

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.*;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.MessageFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.util.resolve.*;
import robostar.robocert.util.resolve.node.ActorNodeResolver;
import robostar.robocert.util.resolve.node.EndpointNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;
import robostar.robocert.util.resolve.node.WorldNodeResolver;

import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests that the {@link EventResolverImpl} seems to be resolving things correctly on {@link
 * ForagingExample}.
 *
 * @author Matt Windsor
 */
class EventResolverTest {

    private EventResolver resolver;
    private final ForagingExample example = new ForagingExample(RoboChartFactory.eINSTANCE);
    private final RoboCertFactory certFactory = RoboCertFactory.eINSTANCE;
    private final MessageFactory msgFactory = new MessageFactory(RoboCertFactory.eINSTANCE);
    private final TargetFactory targetFactory = new TargetFactory(RoboCertFactory.eINSTANCE);

    private World world;
    private ActorEndpoint target;
    private EndpointWrapper wrapper;

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
        final var wNodeRes = new WorldNodeResolver(ctrlRes, modRes, stmRes, aNodeRes, groupFinder);
        final var endRes = new EndpointNodeResolver(ctrlRes, modRes, stmRes, aNodeRes, wNodeRes);
        resolver = new EventResolverImpl(endRes, tgtRes, defRes, ctrlRes, stmRes, groupFinder);

        world = msgFactory.world();
        target = msgFactory.actor(msgFactory.targetActor());

        wrapper = new EndpointWrapper(certFactory, msgFactory);
    }


    /**
     * Tests resolving connections on a module in the example.
     */
    @Test
    void testResolve_module() {
        final var mod = targetFactory.module(example.foraging);
        wrapper.wrap(mod, world, target);

        final var conns1 = resolve(example.platformObstacle, example.obstacleAvoidanceObstacle, world, target);
        assertThat(conns1, hasItems(example.obstaclePlatformToObstacleAvoidance));

        // This connection is not bidirectional, so this should be empty.
        final var conns2 = resolve(example.obstacleAvoidanceObstacle, example.platformObstacle, target, world);
        assertThat(conns2, is(empty()));

        // Inferring an eto.
        final var conns3 = resolve(example.platformObstacle, null, world, target);
        assertThat(conns3, hasItems(example.obstaclePlatformToObstacleAvoidance));
    }

    /**
     * Tests resolving connections on a state machine in the example.
     */
    @Test
    void testResolve_stateMachine() {
        final var stm = targetFactory.stateMachine(example.avoid);
        wrapper.wrap(stm, world, target);

        final var conns1 = resolve(example.obstacleAvoidanceObstacle, example.avoidObstacle, world, target);
        assertThat(conns1, hasItems(example.obstacleObstacleAvoidanceToAvoid));

        // This connection is not bidirectional, so this should be empty.
        final var conns2 = resolve(example.avoidObstacle, example.obstacleAvoidanceObstacle, target, world);
        assertThat(conns2, is(empty()));

        // Inferring an eto.
        final var conns3 = resolve(example.obstacleAvoidanceObstacle, null, world, target);
        assertThat(conns3, hasItems(example.obstacleObstacleAvoidanceToAvoid));
    }

    private Set<Connection> resolve(Event efrom, Event eto, Endpoint from, Endpoint to) {
        final var topic = msgFactory.eventTopic(efrom, eto);
        return resolver.resolve(topic, from, to).collect(Collectors.toUnmodifiableSet());
    }
}
