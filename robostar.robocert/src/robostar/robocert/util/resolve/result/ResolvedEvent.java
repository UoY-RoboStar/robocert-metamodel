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
import robostar.robocert.MessageTopic;
import robostar.robocert.util.resolve.EndIndex;
import robostar.robocert.util.resolve.message.EventResolverQuery;

/**
 * Holds all information about a resolved event.
 *
 * @param query      the query producing this resolved event
 * @param direction  the direction in which the event was resolved
 * @param connection the connection to which the event was resolved
 */
public record ResolvedEvent(EventResolverQuery query, MatchDirection direction,
                            Connection connection) implements ResolvedTopic {

  @Override
  public MessageTopic topic() {
    return query.topic();
  }

  @Override
  public EndIndex effectiveFrom() {
    return EndIndex.From.oppositeIf(effectiveFromIsTo());
  }

  private boolean effectiveFromIsTo() {
    if (query.endpointsAreComponents()) {
      // TODO: It is unclear whether the semantics for BACKWARDS matches is correct.
      return direction == MatchDirection.BACKWARDS;
    } else {
      return query.isFromGate();
    }
  }

}
