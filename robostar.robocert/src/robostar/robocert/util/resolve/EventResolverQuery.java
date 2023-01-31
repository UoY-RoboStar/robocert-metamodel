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
import robostar.robocert.ActorEndpoint;
import robostar.robocert.ComponentActor;
import robostar.robocert.Endpoint;
import robostar.robocert.EventTopic;
import robostar.robocert.Lifeline;

/**
 * A query for event resolution.
 *
 * @param topic     the event topic of the message
 * @param from      the from-endpoint of the message
 * @param to        the to-endpoint of the message
 * @param lifelines the list of lifelines active in this sequence diagram
 */
public record EventResolverQuery(EventTopic topic, Endpoint from, Endpoint to,
                                 List<Lifeline> lifelines) {

  /**
   * Gets whether the two endpoints refer to components.
   *
   * @return true iff both endpoints are ActorEndpoints pointing to ComponentActors.
   */
  public boolean endpointsAreComponents() {
    return endpointIsComponent(from) && endpointIsComponent(to);
  }

  private static boolean endpointIsComponent(Endpoint c) {
    return c instanceof ActorEndpoint e && e.getActor() != null
        && e.getActor() instanceof ComponentActor;
  }
}
