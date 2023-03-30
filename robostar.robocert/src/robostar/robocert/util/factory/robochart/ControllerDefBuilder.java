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

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachine;
import java.util.List;

/**
 * Builder pattern for creating controller definitions.
 *
 * @author Matt Windsor
 */
public class ControllerDefBuilder extends
    AbstractBasicContextBuilder<ControllerDefBuilder, ControllerDef> {

  ControllerDefBuilder(RoboChartFactory chartFac, String name) {
    this.chartFac = chartFac;
    object = chartFac.createControllerDef();
    object.setName(name);
  }

  /**
   * Adds state machines to the controller.
   *
   * @param stateMachines the state machines to add
   * @return a reference to this builder
   */
  public ControllerDefBuilder machines(StateMachine... stateMachines) {
    object.getMachines().addAll(List.of(stateMachines));

    return this;
  }

  // TODO(@MattWindsor91): add more methods as needed

  @Override
  public ControllerDefBuilder self() {
    return this;
  }
}
