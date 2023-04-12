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

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatformDef;

/**
 * Builder pattern for creating robotic platform definitions.
 *
 * @author Matt Windsor
 */
public class RoboticPlatformDefBuilder extends
    AbstractBasicContextBuilder<RoboticPlatformDefBuilder, RoboticPlatformDef> {

  RoboticPlatformDefBuilder(RoboChartFactory factory, String name) {
    this(factory, makeInitialNamed(factory::createRoboticPlatformDef, name));
  }


  RoboticPlatformDefBuilder(RoboChartFactory factory, RoboticPlatformDef initial) {
    super(factory, initial);
  }

  // TODO(@MattWindsor91): add more methods as needed

  @Override
  protected RoboticPlatformDefBuilder selfWith(RoboticPlatformDef newObject) {
    return new RoboticPlatformDefBuilder(chartFactory, newObject);
  }

  @Override
  public RoboticPlatformDefBuilder self() {
    return this;
  }
}
