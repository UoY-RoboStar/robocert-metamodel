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
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.inject.Inject;

import circus.robocalc.robochart.Connection;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.TargetFinder;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.node.TargetNodeResolver;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;
import robostar.robocert.util.resolve.result.ResolvedEvent;
import robostar.robocert.util.resolve.result.ResolvedEvent.Direction;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public record EventResolverImpl(MessageEndNodeResolver endRes, TargetNodeResolver tgtRes,
                                ModuleResolver modRes, ControllerResolver ctrlRes,
                                StateMachineResolver stmRes, TargetFinder targetFinder,
                                OutboundConnectionResolver outRes) implements EventResolver {

  @Inject
  public EventResolverImpl {
    Objects.requireNonNull(ctrlRes);
    Objects.requireNonNull(endRes);
    Objects.requireNonNull(targetFinder);
    Objects.requireNonNull(modRes);
    Objects.requireNonNull(outRes);
    Objects.requireNonNull(stmRes);
    Objects.requireNonNull(tgtRes);
  }

  @Override
  public Stream<ResolvedEvent> resolve(EventResolverQuery q) {
    // Throw if the query is ill-formed:
    q.checkWellFormedness();

    final var target = targetFinder.findOnObject(q.from()).orElseThrow(() -> {
      throw new IllegalArgumentException(
          "can't resolve event if endpoints not within SpecificationGroup");
    });
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
    }.doSwitch(target);
  }

  private Stream<ResolvedEvent> resolveCollection(EventResolverQuery q, CollectionTarget t,
      Stream<Connection> innerConnections) {
    // Collection target connections are more complicated than component target connections, as
    // there are two situations:
    //
    // 1. from a ComponentActor to another ComponentActor, in which case the connection is inside
    //    the target;
    if (q.endpointsAreComponents()) {
      return MatchAttempt.tryMatchMany(q, innerConnections, q.endNodes(endRes));
    }

    // 2. from a ComponentActor to a Gate, in which case we need to proceed as if we were resolving
    //    a component connection from the target to the world instead.
    return resolveOutboundInCollection(q, t);
  }

  private Stream<ResolvedEvent> resolveOutboundInCollection(EventResolverQuery q, Target t) {
    // TODO(@MattWindsor91): make sure we don't need to have a special case for resolving nodes here
    return MatchAttempt.tryMatchMany(q, outRes.resolve(t), q.endNodes(endRes));
  }

  /**
   * Captures an attempt to match a query to a connection.
   *
   * @param query the original resolver query
   * @param conn  the candidate connection
   * @param nodes the pair of sets of nodes representing the ends of the message
   */
  private record MatchAttempt(EventResolverQuery query, Connection conn,
                              MessageEndNodesPair nodes) {

    /**
     * Tries to match a query against multiple possible connections.
     *
     * @param query      the original resolver query
     * @param candidates the stream of connections to consider
     * @param nodes      the pair of sets of nodes representing the ends of the message
     * @return a stream of successfully-matched connections
     */
    public static Stream<ResolvedEvent> tryMatchMany(EventResolverQuery query,
        Stream<Connection> candidates, MessageEndNodesPair nodes) {
      return candidates.flatMap(conn -> new MatchAttempt(query, conn, nodes).tryMatch().stream());
    }

    /**
     * Tries to perform the match described by this attempt record.
     *
     * @return the resolved event, if this match attempt was successful
     */
    public Optional<ResolvedEvent> tryMatch() {
      return matchEnds().filter(this::eventsMatch).map(d -> new ResolvedEvent(query, d, conn));
    }

    /**
     * Checks whether this connection connects the two endpoints.
     *
     * @return the direction in which the connection connects its endpoints, if any.
     */
    private Optional<Direction> matchEnds() {
      if (nodes.matches(conn)) {
        return Optional.of(Direction.FORWARDS);
      }

      if (conn.isBidirec() && nodes.swap().matches(conn)) {
        return Optional.of(Direction.BACKWARDS);
      }

      return Optional.empty();
    }

    private boolean eventsMatch(Direction dir) {
      // TODO(@MattWindsor91): do we need this reversibility here?
      final var cFrom = dir == Direction.FORWARDS ? conn.getEfrom() : conn.getEto();
      final var cTo = dir == Direction.FORWARDS ? conn.getEto() : conn.getEfrom();

      if (!EcoreUtil.equals(query.topic().getEfrom(), cFrom)) {
        return false;
      }
      final var eto = query.topic().getEto();
      return eto == null || EcoreUtil.equals(eto, cTo);
    }

  }
}
