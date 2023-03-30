/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.message;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.resolve.ControllerResolver;
import robostar.robocert.util.resolve.ModuleResolver;
import robostar.robocert.util.resolve.OutboundConnectionResolver;
import robostar.robocert.util.resolve.StateMachineResolver;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;
import robostar.robocert.util.resolve.result.ResolvedEvent;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public record EventResolverImpl(MessageEndNodeResolver endRes, TargetNodeResolver tgtRes,
                                ModuleResolver modRes, ControllerResolver ctrlRes,
                                StateMachineResolver stmRes,
                                OutboundConnectionResolver outRes) implements EventResolver {

  @Inject
  public EventResolverImpl {
    Objects.requireNonNull(ctrlRes);
    Objects.requireNonNull(endRes);
    Objects.requireNonNull(modRes);
    Objects.requireNonNull(outRes);
    Objects.requireNonNull(stmRes);
    Objects.requireNonNull(tgtRes);
  }

  @Override
  public Stream<ResolvedEvent> resolve(EventResolverQuery q) {
    // Throw if the query is ill-formed:
    q.checkWellFormedness();

    return new RoboCertSwitch<Stream<ResolvedEvent>>() {
      @Override
      public Stream<ResolvedEvent> defaultCase(EObject t) {
        throw new IllegalArgumentException("unexpected target type: %s".formatted(t));
      }

      @Override
      public Stream<ResolvedEvent> caseComponentTarget(ComponentTarget t) {
        // Component targets are easy to resolve: all of their connections go from the target to
        // the world, or backwards (and so are outbound in some sense).
        return resolveOutboundInCollection(q, t);
      }

      @Override
      public Stream<ResolvedEvent> caseInModuleTarget(InModuleTarget t) {
        return resolveCollection(q, t, modRes.inboundConnections(t.getModule()));
      }

      @Override
      public Stream<ResolvedEvent> caseInControllerTarget(InControllerTarget t) {
        return resolveCollection(q, t, t.getController().getConnections().stream());
      }
    }.doSwitch(q.ctx().target());
  }

  private Stream<ResolvedEvent> resolveCollection(EventResolverQuery q, CollectionTarget t,
      Stream<Connection> innerConnections) {
    // Collection target connections are more complicated than component target connections, as
    // there are two situations:
    //
    // 1. from a ComponentActor to another ComponentActor, in which case the connection is inside
    //    the target;
    if (q.endpointsAreComponents()) {
      return tryMatchMany(q, innerConnections, q.endNodes(endRes));
    }

    // 2. from a ComponentActor to a Gate, in which case we need to proceed as if we were resolving
    //    a component connection from the target to the world instead.
    return resolveOutboundInCollection(q, t);
  }

  private Stream<ResolvedEvent> resolveOutboundInCollection(EventResolverQuery q, Target t) {
    // TODO(@MattWindsor91): make sure we don't need to have a special case for resolving nodes here
    return tryMatchMany(q, outRes.resolve(t), q.endNodes(endRes));
  }

  /**
   * Tries to match a query against multiple possible connections.
   *
   * @param query      the original resolver query
   * @param candidates the stream of connections to consider
   * @param nodes      the pair of sets of nodes representing the ends of the message
   * @return a stream of successfully-matched connections
   */
  private Stream<ResolvedEvent> tryMatchMany(EventResolverQuery query,
      Stream<Connection> candidates, MessageEndNodesPair nodes) {
    return candidates.flatMap(c -> tryMatchSingle(query, c, nodes));
  }

  private Stream<ResolvedEvent> tryMatchSingle(EventResolverQuery query, Connection candidate,
      MessageEndNodesPair nodes) {
    final var attempt = new EventMatchAttempt(query.topic(), candidate, nodes);
    return attempt.tryMatch().stream().map(d -> new ResolvedEvent(query, d, candidate));
  }
}
