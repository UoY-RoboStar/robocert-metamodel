/* Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Connection;
import java.util.stream.Stream;
import robostar.robocert.Endpoint;
import robostar.robocert.EventTopic;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public interface EventResolver {

  /**
   * Resolves an event to a candidate stream of connections.
   *
   * <p>This stream may contain zero, one, or many connections; typically anything other than one
   * is a well-formedness violation.
   *
   * @param topic the topic of the event to look up.
   * @param from  the from-endpoint of the event's message.
   * @param to    the to-endpoint of the event's message.
   * @return the stream of candidate connections.
   */
  Stream<Connection> resolve(EventTopic topic, Endpoint from, Endpoint to);
}
