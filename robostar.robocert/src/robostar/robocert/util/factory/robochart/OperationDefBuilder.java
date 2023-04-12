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

import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RoboChartFactory;

/**
 * Builder pattern for creating operation definitions.
 *
 * @author Matt Windsor
 */
public class OperationDefBuilder extends
    AbstractBasicContextBuilder<OperationDefBuilder, OperationDef> {

  OperationDefBuilder(RoboChartFactory factory, String name) {
    this(factory, makeInitialNamed(factory::createOperationDef, name));
  }

  OperationDefBuilder(RoboChartFactory factory, OperationDef initial) {
    super(factory, initial);
  }

  // TODO(@MattWindsor91): add more methods as needed

  @Override
  protected OperationDefBuilder selfWith(OperationDef newObject) {
    return new OperationDefBuilder(chartFactory, newObject);
  }

  @Override
  public OperationDefBuilder self() {
    return this;
  }
}
