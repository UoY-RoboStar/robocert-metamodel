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
import com.google.inject.Inject;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.Target;
import robostar.robocert.TargetActor;
import robostar.robocert.util.GroupFinder;

/**
 * Resolves actors into the connection nodes that can represent them.
 *
 * @param tgtRes      resolves targets to connection nodes.
 * @param groupFinder finds groups of objects.
 */
public record ActorNodeResolver(TargetNodeResolver tgtRes, GroupFinder groupFinder) {

    /**
     * Constructs an actor resolver.
     *
     * @param tgtRes      resolves targets to connection nodes.
     * @param groupFinder finds groups of objects.
     */
    @Inject
    public ActorNodeResolver {
        Objects.requireNonNull(tgtRes);
        Objects.requireNonNull(groupFinder);
    }

    /**
     * Resolves an actor to a stream of connection nodes that can represent that actor.
     *
     * <p>The stream may contain more than one node in two situations: either the actor is a target
     * actor and the target is a module (in which case, the module's non-platform components stand in
     * for the module), or the actor is a world (in which case, any of the parent's connection nodes
     * can appear).
     *
     * @param actor the actor to resolve.  Must be attached to a specification group.
     * @return a stream of connection nodes that can represent this actor.
     */
    public Stream<ConnectionNode> resolve(Actor actor) {
        if (actor instanceof ComponentActor c) {
            return Stream.of(c.getNode());
        }
        return target(actor).stream().flatMap(t -> {
            if (actor instanceof TargetActor) {
                return tgtRes.resolve(t);
            }
            throw new IllegalArgumentException("can't resolve actor %s".formatted(actor));
        });
    }

    /**
     * Tries to get the target underlying an actor.
     *
     * <p>Well-formed actors should have a target via their attached specification group.
     *
     * @param actor the actor to inspect.
     * @return the target.
     */
    public Optional<Target> target(Actor actor) {
        // TODO(@MattWindsor91): this should be moved somewhere else, but where?
        return Optional.ofNullable(actor.getGroup()).flatMap(g -> Optional.ofNullable(g.getTarget()));
    }
}
