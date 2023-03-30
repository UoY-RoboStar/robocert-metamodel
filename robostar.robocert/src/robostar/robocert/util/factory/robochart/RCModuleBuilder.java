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
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatform;
import java.util.List;

/**
 * Builder pattern for creating RoboChart modules.
 *
 * @author Matt Windsor
 */
public class RCModuleBuilder extends AbstractBuilder<RCModule> {

  RCModuleBuilder(RoboChartFactory chartFac, String name, RoboticPlatform rp) {
    this.chartFac = chartFac;
    object = chartFac.createRCModule();
    object.setName(name);
    object.getNodes().add(rp);
  }

  /**
   * Adds connections to the RoboChart module being built.
   *
   * @param connections the connections to add to the module
   * @return a reference to the current builder
   */
  public RCModuleBuilder connections(Connection... connections) {
    object.getConnections().addAll(List.of(connections));

    return this;
  }

  /**
   * Adds connection nodes to the RoboChart module being built.
   *
   * @param nodes the nodes to add to the module
   * @return a reference to the current builder
   */
  public RCModuleBuilder nodes(ConnectionNode... nodes) {
    object.getNodes().addAll(List.of(nodes));

    return this;
  }
}
