/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import robostar.robocert.*;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.resolve.node.EndpointNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public record EventResolverImpl(EndpointNodeResolver endRes, TargetNodeResolver tgtRes,
                                ModuleResolver modRes, ControllerResolver ctrlRes,
                                StateMachineResolver stmRes, GroupFinder groupFinder,
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
  public Stream<Connection> resolve(EventResolverQuery q) {
    final var target = groupFinder.findTarget(q.from()).orElseThrow(() -> {
      throw new IllegalArgumentException(
          "can't resolve event if endpoints not within SpecificationGroup");
    });
    return new RoboCertSwitch<Stream<Connection>>() {
      @Override
      public Stream<Connection> defaultCase(EObject t) {
        throw new IllegalArgumentException("unexpected target type: %s".formatted(t));
      }

      @Override
      public Stream<Connection> caseComponentTarget(ComponentTarget t) {
        // Component targets are easy to resolve: all of their connections go from the target to
        // the world, or backwards (and so are outbound in some sense).
        return resolveOutboundInCollection(q, t);
      }

      @Override
      public Stream<Connection> caseInModuleTarget(InModuleTarget t) {
        return resolveCollection(q, t, modRes.inboundConnections(t.getModule()));
      }

      @Override
      public Stream<Connection> caseInControllerTarget(InControllerTarget t) {
        return resolveCollection(q, t, t.getController().getConnections().stream());
      }
    }.doSwitch(target);
  }

  private Stream<Connection> resolveCollection(EventResolverQuery q, CollectionTarget t,
      Stream<Connection> innerConnections) {
    // Collection target connections are more complicated than component target connections, as
    // there are two situations:
    //
    // 1. from a ComponentActor to another ComponentActor, in which case the connection is inside
    //    the target;
    if (q.endpointsAreComponents()) {
      return innerConnections.filter(
          x -> matchesComponent(q, x, endpointNodes(q, q.from()),
              endpointNodes(q, q.to())));
    }

    // 2. from a ComponentActor to a World, in which case we need to proceed as if we were resolving
    //    a component connection from the target to the world instead.
    return resolveOutboundInCollection(q, t);
  }

  private Stream<Connection> resolveOutboundInCollection(EventResolverQuery q, Target t) {
    // WFC CGsA2 has that at least one of these must be the world.
    boolean fromWorld = q.from().isWorld();
    boolean toWorld = q.to().isWorld();

    if (fromWorld && toWorld) {
      throw new IllegalArgumentException(
          "tried to resolve connection with two Worlds");
    }
    if (!fromWorld && !toWorld) {
      throw new IllegalArgumentException(
          "tried to resolve connection with only two TargetActors - violates CGsA2");
    }

    final var tnodes = targetNodes(t);
    final var from = fromWorld ? endpointNodes(q, q.from()) : tnodes;
    final var to = toWorld ? tnodes : endpointNodes(q, q.to());

    return outRes.resolve(t).filter(x -> matchesComponent(q, x, from, to));
  }

  private Set<ConnectionNode> endpointNodes(EventResolverQuery q, Endpoint e) {
    return endRes.resolve(e, q.lifelines()).collect(Collectors.toUnmodifiableSet());
  }

  private Set<ConnectionNode> targetNodes(Target t) {
    return tgtRes.resolve(t).collect(Collectors.toUnmodifiableSet());
  }

  private boolean matchesComponent(EventResolverQuery q, Connection c, Set<ConnectionNode> from,
      Set<ConnectionNode> to) {
    if (!endpointsMatch(c, from, to)) {
      return false;
    }
    // TODO(@MattWindsor91): do we need reversibility here?
    if (!EcoreUtil.equals(q.topic().getEfrom(), c.getEfrom())) {
      return false;
    }
    final var eto = q.topic().getEto();
    return eto == null || EcoreUtil.equals(eto, c.getEto());
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
}
