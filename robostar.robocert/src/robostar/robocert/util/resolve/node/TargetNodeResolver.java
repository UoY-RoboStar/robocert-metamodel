/* Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.node;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.RoboticPlatform;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;

import java.util.stream.Stream;

/**
 * Resolves targets into the connection nodes that can represent them.
 *
 * @author Matt Windsor
 */
public class TargetNodeResolver {
    /**
     * Deduces a stream of connection nodes that can represent the target actor for a target.
     *
     * <p>The stream may contain more than one node if the target is a module (in which case, the
     * module's non-platform components stand in for the module).
     *
     * @param target the target for which we are resolving target-relative actors.
     * @return a stream of connection nodes that can represent the target actor.
     */
    public Stream<ConnectionNode> resolve(Target target) {
        return new RoboCertSwitch<Stream<ConnectionNode>>() {
            @Override
            public Stream<ConnectionNode> defaultCase(EObject e) {
                throw new IllegalArgumentException("can't resolve actor for target %s".formatted(e));
            }

            @Override
            public Stream<ConnectionNode> caseHasModuleTarget(HasModuleTarget t) {
                // Modules don't have a single connection node, as they are the top-level container for nodes.
                // Instead, we note that everything a module connects to the platform is effectively a
                // surrogate node for the module.
                // TODO(@MattWindsor91): I don't think this behaviour is ever useful!?
                return t.getModule().getNodes().stream().filter(x -> !(x instanceof RoboticPlatform));
            }

            @Override
            public Stream<ConnectionNode> caseHasControllerTarget(HasControllerTarget t) {
                return Stream.of(t.getController());
            }

            @Override
            public Stream<ConnectionNode> caseStateMachineTarget(StateMachineTarget t) {
                return Stream.of(t.getStateMachine());
            }

            @Override
            public Stream<ConnectionNode> caseOperationTarget(OperationTarget t) {
                return Stream.of(t.getOperation());
            }

            // TODO(@MattWindsor91): GitHub #124: these can likely be extended.
        }.doSwitch(target);

        // Despite WFC CGsA2, resolving on collection targets can happen if we're resolving namespaces for subcomponent
        // events, operations etc.
    }
}
