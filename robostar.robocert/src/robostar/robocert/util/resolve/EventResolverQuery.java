/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import java.util.List;
import robostar.robocert.Actor;
import robostar.robocert.Message;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.ComponentActor;
import robostar.robocert.MessageEnd;
import robostar.robocert.EventTopic;
import robostar.robocert.util.resolve.node.MessageEndNodeResolver;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;

/**
 * A query for event resolution.
 *
 * @param message the message
 * @param topic   the pre-selected event topic of the message
 * @param actors  the list of actors active on the specification group
 */
public record EventResolverQuery(Message message, EventTopic topic, List<Actor> actors) {

  /**
   * @return the from-end of the message
   */
  public MessageEnd from() {
    return message.getFrom();
  }

  /**
   * @return the to-end of the message
   */
  public MessageEnd to() {
    return message.getTo();
  }

  /**
   * Gets whether the two endpoints refer to components.
   *
   * @return true iff both endpoints are MessageOccurrences pointing to ComponentActors
   */
  public boolean endpointsAreComponents() {
    return endpointIsComponent(from()) && endpointIsComponent(to());
  }

  private static boolean endpointIsComponent(MessageEnd c) {
    if (!(c instanceof MessageOccurrence e)) {
      return false;
    }
    return e.getActor() instanceof ComponentActor;
  }

  /**
   * @return true if, and only if, the from-end is a gate.
   */
  public boolean isFromGate() {
    return from().isGate();
  }

  /**
   * @return true if, and only if, the to-end is a gate.
   */
  public boolean isToGate() {
    return to().isGate();
  }

  /**
   * Checks whether this query has potential well-formedness issues.
   *
   * @throws IllegalArgumentException if the query is ill-formed (for instance, both its ends are
   *                                  gates, or both name target actors)
   */
  public void checkWellFormedness() {
    // TODO(@MattWindsor91): it's unclear as to whether we should treat these as well-formedness
    // conditions to break the generator on, or just reasons to (silently) abandon resolution.

    final var fromGate = isFromGate();
    final var toGate = isToGate();

    if (fromGate && toGate) {
      throw new IllegalArgumentException("tried to resolve connection with two Gates");
    }

    final var fromComp = endpointIsComponent(from());
    final var fromCompOrGate = fromComp || fromGate;
    final var fromTarget = !fromCompOrGate;

    final var toComp = endpointIsComponent(to());
    final var toCompOrGate = toComp || toGate;
    final var toTarget = !toCompOrGate;

    if ((fromComp && toTarget) || (toComp && fromTarget)) {
      throw new IllegalArgumentException("tried to mix target and component actors");
    }

    if (toTarget && fromTarget) {
      throw new IllegalArgumentException("tried to resolve connection with two TargetActors");
    }
  }

  /**
   * Resolves the message end nodes pair for this query.
   *
   * @param res the resolver to use
   * @return the message end nodes pair for the query's message considering the current actors
   */
  public MessageEndNodesPair endNodes(MessageEndNodeResolver res) {
    return res.resolvePair(message, actors);
  }
}
