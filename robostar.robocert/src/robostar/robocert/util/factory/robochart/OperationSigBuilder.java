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

import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;

/**
 * Builder pattern for creating robotic platform definitions.
 *
 * @author Matt Windsor
 */
public class OperationSigBuilder extends
    AbstractRoboChartBuilder<OperationSigBuilder, OperationSig> {

  OperationSigBuilder(RoboChartFactory factory, String name) {
    this(factory, makeInitialNamed(factory::createOperationSig, name));
  }

  OperationSigBuilder(RoboChartFactory factory, OperationSig initial) {
    super(factory, initial);
  }

  /**
   * Adds parameters to the definition being built.
   *
   * @param parameters the parameters to add to the definition
   * @return a reference to the current builder
   */
  public OperationSigBuilder parameters(Parameter... parameters) {
    object.getParameters().addAll(List.of(parameters));

    return this;
  }

  @Override
  protected OperationSigBuilder selfWith(OperationSig newObject) {
    return new OperationSigBuilder(chartFactory, newObject);
  }

  @Override
  protected OperationSigBuilder self() {
    return this;
  }

  // TODO(@MattWindsor91): add more methods as needed
}
