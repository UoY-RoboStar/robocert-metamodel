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
public class RCModuleBuilder extends AbstractRoboChartBuilder<RCModuleBuilder, RCModule> {

  RCModuleBuilder(RoboChartFactory factory, String name, RoboticPlatform rp) {
    this(factory, makeInitial(factory, name, rp));
  }

  RCModuleBuilder(RoboChartFactory factory, RCModule initial) {
    super(factory, initial);
  }

  private static RCModule makeInitial(RoboChartFactory factory, String name, RoboticPlatform rp) {
    final var initial = factory.createRCModule();
    initial.setName(name);
    initial.getNodes().add(rp);

    return initial;
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

  @Override
  protected RCModuleBuilder selfWith(RCModule newObject) {
    return new RCModuleBuilder(chartFactory, newObject);
  }

  @Override
  protected RCModuleBuilder self() {
    return this;
  }
}
