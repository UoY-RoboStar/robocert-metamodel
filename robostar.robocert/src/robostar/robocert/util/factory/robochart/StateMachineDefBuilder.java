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
import circus.robocalc.robochart.StateMachineDef;

/**
 * Builder pattern for creating state machine definitions.
 *
 * @author Matt Windsor
 */
public class StateMachineDefBuilder extends
    AbstractBasicContextBuilder<StateMachineDefBuilder, StateMachineDef> {

  StateMachineDefBuilder(RoboChartFactory factory, String name) {
    this(factory, makeInitialNamed(factory::createStateMachineDef, name));
  }

  StateMachineDefBuilder(RoboChartFactory factory, StateMachineDef initial) {
    super(factory, initial);
  }

  // TODO(@MattWindsor91): add more methods as needed

  @Override
  protected StateMachineDefBuilder selfWith(StateMachineDef newObject) {
    return new StateMachineDefBuilder(chartFactory, newObject);
  }

  @Override
  public StateMachineDefBuilder self() {
    return this;
  }
}
