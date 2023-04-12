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

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Type;
import circus.robocalc.robochart.Variable;

/**
 * Builder pattern for creating variables.
 *
 * @author Matt Windsor
 */
public class VariableBuilder extends AbstractRoboChartBuilder<VariableBuilder, Variable> {
  VariableBuilder(RoboChartFactory chartFac, String name, Type type) {
    this(chartFac, makeInitial(chartFac, name, type));
  }

  VariableBuilder(RoboChartFactory chartFac, Variable initial) {
    super(chartFac, initial);
  }

  private static Variable makeInitial(RoboChartFactory chartFac, String name, Type type) {
    final var initial = chartFac.createVariable();
    initial.setName(name);
    initial.setType(type);
    return initial;
  }

  public VariableBuilder initial(Expression expression) {
    object.setInitial(expression);

    return this;
  }

  @Override
  protected VariableBuilder selfWith(Variable newObject) {
    return new VariableBuilder(chartFactory, newObject);
  }

  @Override
  protected VariableBuilder self() {
    return this;
  }

  // We do not expose setModifier; modifiers are set by the parent VariableList.
}
