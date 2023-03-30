/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.message;

import circus.robocalc.robochart.Connection;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.EventTopic;
import robostar.robocert.util.resolve.EndIndex;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;
import robostar.robocert.util.resolve.result.MatchDirection;

/**
 * Captures an attempt to match an event topic to a connection.
 *
 * @param topic the topic of the event
 * @param conn  the candidate connection
 * @param nodes the pair of sets of nodes representing the ends of the message
 */
public record EventMatchAttempt(EventTopic topic, Connection conn, MessageEndNodesPair nodes) {

  /**
   * Tries to perform the match described by this attempt record.
   *
   * @return the direction in which the match occurred, if this attempt was successful
   */
  public Optional<MatchDirection> tryMatch() {
    return matchEnds().filter(this::eventsMatch);
  }

  /**
   * Checks whether this connection connects the two endpoints.
   *
   * @return the direction in which the connection connects its endpoints, if any.
   */
  private Optional<MatchDirection> matchEnds() {
    if (nodes.matches(conn)) {
      return Optional.of(MatchDirection.FORWARDS);
    }

    if (conn.isBidirec() && nodes.swap().matches(conn)) {
      return Optional.of(MatchDirection.BACKWARDS);
    }

    return Optional.empty();
  }

  private boolean eventsMatch(MatchDirection dir) {
    return endMatches(dir, EndIndex.From) && endMatches(dir, EndIndex.To);
  }

  private boolean endMatches(MatchDirection dir, EndIndex endIndex) {
    final var connEnd = endIndex.oppositeIf(dir == MatchDirection.BACKWARDS).eventOf(conn);
    final var topicEnd = Objects.requireNonNullElse(endIndex.eventOf(topic), connEnd);
    return EcoreUtil.equals(topicEnd, connEnd);
  }
}
