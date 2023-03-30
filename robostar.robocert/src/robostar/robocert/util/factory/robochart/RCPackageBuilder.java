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

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;

/**
 * Builder pattern for creating RoboChart packages.
 *
 * @author Matt Windsor
 */
public class RCPackageBuilder extends AbstractBuilder<RCPackage> {

  RCPackageBuilder(RoboChartFactory chartFac, String name) {
    this.chartFac = chartFac;
    object = chartFac.createRCPackage();
    object.setName(name);
  }

  /**
   * Adds modules to the RoboChart package being built.
   *
   * @return a reference to the current builder
   */
  public RCPackageBuilder modules(RCModule... modules) {
    object.getModules().addAll(List.of(modules));

    return this;
  }

  // TODO(@MattWindsor91): add more methods here.
}
