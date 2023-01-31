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
import robostar.robocert.MessageOccurrence;
import robostar.robocert.ComponentActor;
import robostar.robocert.MessageEnd;
import robostar.robocert.EventTopic;

/**
 * A query for event resolution.
 *
 * @param topic  the event topic of the message
 * @param from   the from-endpoint of the message
 * @param to     the to-endpoint of the message
 * @param actors the list of actors active on the specification group
 */
public record EventResolverQuery(EventTopic topic, MessageEnd from, MessageEnd to,
                                 List<Actor> actors) {

  /**
   * Gets whether the two endpoints refer to components.
   *
   * @return true iff both endpoints are MessageOccurrences pointing to ComponentActors.
   */
  public boolean endpointsAreComponents() {
    return endpointIsComponent(from) && endpointIsComponent(to);
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
    return from.isGate();
  }

  /**
   * @return true if, and only if, the to-end is a gate.
   */
  public boolean isToGate() {
    return to.isGate();
  }
}
