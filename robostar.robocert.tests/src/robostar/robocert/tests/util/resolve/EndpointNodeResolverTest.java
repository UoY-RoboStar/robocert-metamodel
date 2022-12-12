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

import java.util.Set;
import java.util.stream.Collectors;

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

/**
 * Tests that the {@link ActorNodeResolver} seems to be resolving things correctly on
 * {@link ForagingExample}.
 *
 * @author Matt Windsor
 */
class EndpointNodeResolverTest {


    private EndpointNodeResolver resolver;
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
        final var wNodeRes = new WorldNodeResolver(modRes, ctrlRes, stmRes, aNodeRes, groupFinder);
        resolver = new EndpointNodeResolver(aNodeRes, wNodeRes);

        world = msgFactory.world();
        target = msgFactory.actor(msgFactory.targetActor());

        wrapper = new EndpointWrapper(certFactory, msgFactory);
    }

    /**
     * Tests resolving connection nodes on a state machine in the example.
     */
    @Test
    void testResolve_stateMachine() {
        final var stm = targetFactory.stateMachine(example.avoid);
        wrapper.wrap(stm, world, target);

        final var worldNodes = resolve(world);
        assertThat(worldNodes, hasItems(example.platform, example.obstacleAvoidance));

        final var targetNodes = resolve(target);
        assertThat(targetNodes, hasItems(example.avoid));
    }

    private Set<ConnectionNode> resolve(Endpoint a) {
        return resolver.resolve(a).collect(Collectors.toUnmodifiableSet());
    }
}
