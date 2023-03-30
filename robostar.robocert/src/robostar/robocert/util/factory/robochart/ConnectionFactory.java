/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory.robochart;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.Objects;
import javax.inject.Inject;


/**
 * Factory for producing RoboChart connections.
 *
 * @param chartFac the underlying RoboChart factory
 *
 * @author Matt Windsor
 */
public record ConnectionFactory(RoboChartFactory chartFac) {
  @Inject
  public ConnectionFactory {
    Objects.requireNonNull(chartFac, "RoboChart factory must be non-null");
  }

  /**
   * Starts building a {@link Connection} by supplying the from-node and from-event.
   *
   * @param node the from-node
   * @param event the from-event
   * @return the connection builder, to which one must supply the to-node and to-event
   */
  public From from(ConnectionNode node, Event event) {
    return new From(node, event);
  }

  /**
   * Intermediate stage of connection building.
   *
   * @author Matt Windsor
   */
  public class From {
     private final ConnectionNode node;
    private final Event event;

    private From(ConnectionNode node, Event event) {
      this.node = Objects.requireNonNull(node, "from-node must be non-null");
      this.event = Objects.requireNonNull(event, "from-event must be non-null");
    }

    /**
     * Completes a {@link Connection} by supplying the to-node and to-event.
     *
     * @param node the to-node
     * @param event the to-event
     * @return the finished connection
     */
    public Connection to(ConnectionNode node, Event event) {
      final var result = chartFac.createConnection();
      result.setFrom(this.node);
      result.setEfrom(this.event);

      result.setTo(Objects.requireNonNull(node, "to-node must be non-null"));
      result.setEto(Objects.requireNonNull(event, "to-event must be non-null"));

      return result;
    }
  }
}
