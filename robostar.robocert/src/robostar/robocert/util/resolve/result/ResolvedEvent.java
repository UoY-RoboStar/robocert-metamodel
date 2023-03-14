/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.result;

import circus.robocalc.robochart.Connection;
import robostar.robocert.MessageEnd;
import robostar.robocert.util.resolve.EventResolverQuery;

/**
 * Holds all information about a resolved event.
 *
 * @param query      the query producing this resolved event
 * @param direction  the direction in which the event was resolved
 * @param connection the connection to which the event was resolved
 */
public record ResolvedEvent(EventResolverQuery query, Direction direction, Connection connection) {

  /**
   * Gets the 'effective' from-end of this message.
   *
   * <p>
   * This end is important because, in certain definitions of the RoboChart semantics (for instance,
   * rule 15 of the CSP semantics), the to-end of synchronous messages is subsumed into the from-end
   * for synchronisation.
   *
   * <p>
   * We define the effective from-end for messages involving a gate as whichever end is not that
   * gate.  Otherwise, we define it as the actual from-end if we matched forwards, and the actual
   * to-end if we matched backwards.
   *
   * @return the effective from-end.
   */
  public MessageEnd effectiveFrom() {
    var swap = false;

    if (query.endpointsAreComponents()) {
      // TODO: It is unclear whether the semantics for BACKWARDS matches is correct.
      swap = direction == Direction.BACKWARDS;
    } else {
      swap = query.isFromGate();
    }

    return swap ? query.to() : query.from();
  }

  /**
   * Enumeration of directions in which an event can be resolved.
   *
   * @author Matt Windsor
   */
  public enum Direction {
    /**
     * The query matched this event directly, without swapping direction.
     */
    FORWARDS,
    /**
     * The query matched this (bidirectional) event by swapping direction.
     */
    BACKWARDS;
  }
}
