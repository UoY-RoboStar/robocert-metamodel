/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */

package robostar.robocert.util.resolve;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import robostar.robocert.*;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.resolve.node.EndpointNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public record EventResolverImpl(EndpointNodeResolver endRes, TargetNodeResolver tgtRes, ModuleResolver modRes,
                                ControllerResolver ctrlRes, StateMachineResolver stmRes, GroupFinder groupFinder,
                                OutboundConnectionResolver outRes) implements EventResolver {

    @Inject
    public EventResolverImpl {
        Objects.requireNonNull(ctrlRes);
        Objects.requireNonNull(endRes);
        Objects.requireNonNull(groupFinder);
        Objects.requireNonNull(modRes);
        Objects.requireNonNull(outRes);
        Objects.requireNonNull(stmRes);
        Objects.requireNonNull(tgtRes);
    }

    @Override
    public Stream<Connection> resolve(EventTopic topic, Endpoint from, Endpoint to) {
        final var target = groupFinder.findTarget(from).orElseThrow(() -> {
            throw new IllegalArgumentException("can't resolve event if endpoints not within SpecificationGroup");
        });
        if (target instanceof ComponentTarget t) {
            return resolveComponent(topic, from, to, t);
        }
        if (target instanceof CollectionTarget t) {
            return resolveCollection(topic, from, to, t);
        }
        throw new IllegalArgumentException("target neither component nor collection: %s".formatted(target));
    }

    private Stream<Connection> resolveComponent(EventTopic topic, Endpoint from, Endpoint to, Target t) {
        // Component targets are easy to resolve: all of their connections go from the target to
        // the world, or backwards (and so are outbound in some sense).
        return resolveOutbound(topic, endpointNodes(from), endpointNodes(to), t);
    }

    private Stream<Connection> resolveCollection(EventTopic topic, Endpoint from, Endpoint to, CollectionTarget t) {
        // Collection target connections are more complicated than component target connections, as
        // there are two situations:
        //
        // - from a ComponentActor to another ComponentActor, in which case the connection is inside
        //   the target;
        // - from a ComponentActor to a World, in which case we need to proceed as if we were resolving
        //   a component connection from the target to the world instead.
        if (isComponentActor(from) && isComponentActor(to)) {
            return innerConnections(t).filter(x -> matchesComponent(x, topic, endpointNodes(from), endpointNodes(to)));
        }

        // WFC CGsA2 has that at least one of these must be the world.
        if (from.isWorld()) {
            return resolveOutbound(topic, endpointNodes(from), targetNodes(t), t);
        }
        if (to.isWorld()) {
            return resolveOutbound(topic, targetNodes(t), endpointNodes(to), t);
        }

        throw new IllegalArgumentException("tried to resolve collection with TargetActors - violates CGsA2");
    }

    private boolean isComponentActor(Endpoint x) {
        return x instanceof ActorEndpoint e && e.getActor() != null && e.getActor() instanceof ComponentActor;
    }

    private Stream<Connection> resolveOutbound(EventTopic topic, Set<ConnectionNode> from, Set<ConnectionNode> to, Target t) {
        return outRes.resolve(t).filter(x -> matchesComponent(x, topic, from, to));
    }

    private Set<ConnectionNode> endpointNodes(Endpoint from) {
        return endRes.resolve(from).collect(Collectors.toUnmodifiableSet());
    }

    private Set<ConnectionNode> targetNodes(Target t) {
        return tgtRes.resolve(t).collect(Collectors.toUnmodifiableSet());
    }

    private boolean matchesComponent(Connection c, EventTopic topic, Set<ConnectionNode> from, Set<ConnectionNode> to) {
        if (!endpointsMatch(c, from, to)) {
            return false;
        }
        // TODO(@MattWindsor91): do we need reversibility here?
        if (!EcoreUtil.equals(topic.getEfrom(), c.getEfrom())) {
            return false;
        }
        final var eto = topic.getEto();
        return eto == null || EcoreUtil.equals(topic.getEto(), c.getEto());
    }

    /**
     * Checks whether this connection connects the two endpoints.
     *
     * @param c    connection to investigate.
     * @param from set of nodes allowed at the 'from' endpoint.
     * @param to   set of nodes allowed at the 'to' endpoint.
     * @return whether c connects nodes represented by from and to, in the appropriate direction.
     */
    private boolean endpointsMatch(Connection c, Set<ConnectionNode> from, Set<ConnectionNode> to) {
        return nodesMatch(c, from, to) || (c.isBidirec() && nodesMatch(c, to, from));
    }

    private boolean nodesMatch(Connection c, Set<ConnectionNode> from, Set<ConnectionNode> to) {
        return from.contains(c.getFrom()) && to.contains(c.getTo());
    }

    /**
     * Gets the connections between components inside this target.
     *
     * @param target the collection target whose connections we are searching.
     * @return the stream of connections defined between this target's components.
     */
    private Stream<Connection> innerConnections(CollectionTarget target) {
        if (target instanceof InModuleTarget m) {
            return modRes.inboundConnections(m.getModule());
        }
        if (target instanceof InControllerTarget c) {
            return c.getController().getConnections().stream();
        }

        throw new IllegalArgumentException("can't get inner connections of %s".formatted(target));
    }

}
