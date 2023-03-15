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
import circus.robocalc.robochart.ConnectionNode;
import java.util.Set;

/**
 * A pair of message end node sets, representing the result of resolving the nodes underlying both
 * ends of a message.
 *
 * @param from the connection nodes representing a from-end.
 * @param to   the connection nodes representing a to-end.
 * @author Matt Windsor
 */
public record MessageEndNodesPair(Set<ConnectionNode> from, Set<ConnectionNode> to) {
  // TODO(@MattWindsor91): we shouldn't enumerate the set of nodes that form a Gate.
  // Instead, we should restrict matches onto nodes that are known *not* to be a Gate.

  /**
   * @return a node pair with the from- and to-set swapped
   */
  public MessageEndNodesPair swap() {
    return new MessageEndNodesPair(to, from);
  }

  /**
   * Checks whether a connection's from- and to- nodes lie within the sets in this pair.
   *
   * @param conn the connection to check
   * @return true if the from-set contains the connection's from-node and the to-set contains the
   * connection's to-node
   */
  public boolean matches(Connection conn) {
    return from.contains(conn.getFrom()) && to.contains(conn.getTo());
  }
}
