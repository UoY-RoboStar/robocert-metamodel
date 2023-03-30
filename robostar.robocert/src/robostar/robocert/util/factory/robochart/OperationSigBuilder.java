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
public class OperationSigBuilder extends AbstractBuilder<OperationSig> {

  OperationSigBuilder(RoboChartFactory chartFac, String name) {
    this.chartFac = chartFac;
    object = chartFac.createOperationSig();
    object.setName(name);
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

  // TODO(@MattWindsor91): add more methods as needed
}
