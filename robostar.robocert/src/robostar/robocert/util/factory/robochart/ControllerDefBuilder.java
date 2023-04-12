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
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Operation;
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

  ControllerDefBuilder(RoboChartFactory factory, String name) {
    this(factory, makeInitialNamed(factory::createControllerDef, name));
  }

  ControllerDefBuilder(RoboChartFactory factory, ControllerDef initial) {
    super(factory, initial);
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


  /**
   * Adds locally-defined operations to the controller.
   *
   * @param operations the operation definitions to add
   * @return a reference to this builder
   */
  public ControllerDefBuilder lOperations(Operation... operations) {
    object.getLOperations().addAll(List.of(operations));

    return this;
  }

  /**
   * Adds connections to the controller.
   *
   * @param connections the connections to add
   * @return a reference to this builder
   */
  public ControllerDefBuilder connections(Connection... connections) {
    object.getConnections().addAll(List.of(connections));

    return this;
  }

  // TODO(@MattWindsor91): add more methods as needed

  @Override
  protected ControllerDefBuilder selfWith(ControllerDef newObject) {
    return new ControllerDefBuilder(chartFactory, newObject);
  }

  @Override
  public ControllerDefBuilder self() {
    return this;
  }
}
