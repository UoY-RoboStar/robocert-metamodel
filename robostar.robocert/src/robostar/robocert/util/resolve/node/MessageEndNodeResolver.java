/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.node;

import circus.robocalc.robochart.*;
import com.google.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;

import java.util.Objects;
import java.util.stream.Stream;
import robostar.robocert.util.StreamHelper;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;

/**
 * Resolves message ends into the connection nodes that can represent them.
 *
 * @param aNodeRes helper for resolving actors into connection nodes.
 * @param wNodeRes helper for resolving worlds into connection nodes.
 */
public record MessageEndNodeResolver(ActorNodeResolver aNodeRes, WorldNodeResolver wNodeRes) {

  /**
   * Constructs an actor resolver.
   *
   * @param aNodeRes helper for resolving actors into connection nodes.
   * @param wNodeRes helper for resolving worlds into connection nodes.
   */
  @Inject
  public MessageEndNodeResolver {
    Objects.requireNonNull(aNodeRes);
    Objects.requireNonNull(wNodeRes);
  }

  /**
   * Resolves a message to a pair of sets of connection nodes that can represent its endpoints.
   *
   * @param message the message to resolve; must be attached to a specification group
   * @param actors  the list of actors to consider for occurrences and exclude for gates
   * @return a pair of sets of connection nodes that can represent the message's endpoints, given
   * the specified actors
   */
  public MessageEndNodesPair resolvePair(Message message, List<Actor> actors) {
    final var from = resolve(message.getFrom(), actors).collect(Collectors.toUnmodifiableSet());
    final var to = resolve(message.getTo(), actors).collect(Collectors.toUnmodifiableSet());

    return new MessageEndNodesPair(from, to);
  }

  /**
   * Resolves an end to a stream of connection nodes that can represent that endpoint.
   *
   * <p>The stream may contain more than one node in two situations: either the endpoint is a
   * target endpoint and the target is a module (in which case, the module's non-platform components
   * stand in for the module), or the endpoint is a world (in which case, any of the parent's
   * connection nodes can appear).
   *
   * @param endpoint the endpoint to resolve; must be attached to a specification group
   * @param actors   the list of actors to consider for occurrences and exclude for gates
   * @return a stream of connection nodes that can represent this endpoint, given the specified
   * actors
   */
  public Stream<ConnectionNode> resolve(MessageEnd endpoint, List<Actor> actors) {
    final var actorNodes = actors.stream().flatMap(aNodeRes::resolve)
        .collect(Collectors.toUnmodifiableSet());

    return new RoboCertSwitch<Stream<ConnectionNode>>() {
      @Override
      public Stream<ConnectionNode> defaultCase(EObject e) {
        throw new IllegalArgumentException("can't resolve endpoint %s".formatted(e));
      }

      @Override
      public Stream<ConnectionNode> caseMessageOccurrence(MessageOccurrence occ) {
        // TODO(@MattWindsor91): technically this is duplicating 'lifelineNodes', but I can't think
        // of a better way of doing this without overly complicating the resolver.
        final var allNodes = aNodeRes.resolveInOccurrence(occ);

        // Any actors referenced by an occurrence should be actors in the diagram.
        // This should really be guaranteed by well-formedness, but we double-check here anyway.
        return allNodes.filter(actorNodes::contains);
      }

      @Override
      public Stream<ConnectionNode> caseGate(Gate g) {
        final var allNodes = wNodeRes.resolveInGate(g);
        // Any actors referenced by a gate should NOT be actors in the diagram.
        // Unlike above, we absolutely need to filter `allNodes`.
        return StreamHelper.exclude(allNodes, actorNodes::contains);
      }
    }.doSwitch(endpoint);
  }
}
