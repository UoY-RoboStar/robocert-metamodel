/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.node;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.StateMachineBody;
import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.StreamHelper;
import robostar.robocert.util.resolve.ControllerResolver;
import robostar.robocert.util.resolve.ModuleResolver;
import robostar.robocert.util.resolve.StateMachineResolver;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Resolves worlds into the connection nodes that can represent them.
 *
 * @param modRes      helper for resolving aspects of RoboChart modules.
 * @param ctrlRes     helper for resolving aspects of RoboChart controllers.
 * @param stmRes      helper for resolving aspects of RoboChart state machines and operations.
 * @param aNodeRes    helper for resolving actors into connection nodes.
 * @param groupFinder helper for finding enclosing groups of endpoints.
 */
public record WorldNodeResolver(ModuleResolver modRes, ControllerResolver ctrlRes, StateMachineResolver stmRes,
                                ActorNodeResolver aNodeRes, GroupFinder groupFinder) {
    // TODO(@MattWindsor91): DRY up with the other NodeResolvers.

    /**
     * Constructs an actor resolver.
     *
     * @param ctrlRes     helper for resolving aspects of RoboChart controllers.
     * @param modRes      helper for resolving aspects of RoboChart modules.
     * @param stmRes      helper for resolving aspects of RoboChart state machines and operations.
     * @param aNodeRes    helper for resolving actors into connection nodes.
     * @param groupFinder helper for finding enclosing groups of endpoints.
     */
    @Inject
    public WorldNodeResolver {
        Objects.requireNonNull(modRes);
        Objects.requireNonNull(ctrlRes);
        Objects.requireNonNull(stmRes);
        Objects.requireNonNull(aNodeRes);
        Objects.requireNonNull(groupFinder);
    }

    /**
     * Resolves a world to a stream of connection nodes that can represent that endpoint.
     *
     * @param w the world to resolve.  Must be attached to a specification group.
     * @return a stream of connection nodes that can represent this endpoint.
     */
    public Stream<ConnectionNode> resolve(World w) {
        return groupFinder.findTarget(w).stream().flatMap(this::resolveFromTarget);
    }

    /**
     * Deduces a stream of connection nodes that can represent the world actor for a target.
     *
     * <p>The stream may contain more than one node.
     *
     * @param target the target for which we are resolving target-relative actors.
     * @return a stream of connection nodes that can represent the target actor.
     */
    public Stream<ConnectionNode> resolveFromTarget(Target target) {
        return new RoboCertSwitch<Stream<ConnectionNode>>() {
            @Override
            public Stream<ConnectionNode> defaultCase(EObject e) {
                throw new IllegalArgumentException("can't resolve world actor for target %s".formatted(e));
            }

            @Override
            public Stream<ConnectionNode> caseHasModuleTarget(HasModuleTarget t) {
                // The world of a module is just its platform (with some casting to ConnectionNode).
                return modRes.platform(t.getModule()).stream().map(x -> x);
            }

            @Override
            public Stream<ConnectionNode> caseHasControllerTarget(HasControllerTarget t) {
                final var ctrl = t.getController();
                final var mod = ctrlRes.module(ctrl);
                // The world of a controller is everything visible inside its module, except the controller
                // itself.
                return mod.stream().flatMap(m -> {
                    // The world of a module is just its platform (with some casting to ConnectionNode).
                    final var above = modRes.platform(m).stream().<ConnectionNode>map(x1 -> x1);
                    final var local = m.getNodes().stream();
                    return Stream.concat(above, local.filter(x -> x != ctrl));
                });
            }

            @Override
            public Stream<ConnectionNode> caseStateMachineTarget(StateMachineTarget t) {
                return stmBodyWorld(t.getStateMachine());
            }

            @Override
            public Stream<ConnectionNode> caseOperationTarget(OperationTarget t) {
                return stmBodyWorld(t.getOperation());
            }
        }.doSwitch(target);
    }

    private Stream<ConnectionNode> stmBodyWorld(StateMachineBody s) {
        // The world of a state machine or operation is everything visible inside its controller,
        // except the state machine body itself.
        return stmRes.controller(s).stream().flatMap(c -> {
            // The world of a controller is everything visible inside its module, except the controller
            // itself.
            final var above = StreamHelper.push(c, ctrlRes.module(c).stream().flatMap(m -> {
                // The world of a module is just its platform (with some casting to ConnectionNode).
                final var above1 = modRes.platform(m).stream().<ConnectionNode>map(x1 -> x1);
                final var local1 = m.getNodes().stream();
                return Stream.concat(above1, local1.filter(x1 -> x1 != c));
            }));
            final var local = Stream.concat(c.getLOperations().stream(), c.getMachines().stream());
            return Stream.concat(above, local.filter(x -> x != s));
        });
    }
}
