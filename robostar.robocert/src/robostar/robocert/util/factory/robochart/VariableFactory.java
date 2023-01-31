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
import circus.robocalc.robochart.VariableList;
import circus.robocalc.robochart.VariableModifier;
import com.google.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * High-level factory for RoboChart variables.
 *
 * @param chartFac the underlying RoboChart factory
 * @author Matt Windsor
 */
public record VariableFactory(RoboChartFactory chartFac) {

  /**
   * The default variable factory.
   */
  public static final VariableFactory DEFAULT = new VariableFactory(RoboChartFactory.eINSTANCE);

  /**
   * Injectable constructor for variable factories.
   *
   * @param chartFac underlying RoboChart factory.
   */
  @Inject
  public VariableFactory {
    Objects.requireNonNull(chartFac);
  }

  /**
   * Constructs a variable list with the given variables.
   *
   * @param modifier modifier (CONST or VAR) for the variable list.
   * @param vars     variables to put in the variable list.
   * @return variable list with the given modifier and variables.
   */
  public VariableList list(VariableModifier modifier, Variable... vars) {
    return list(modifier, List.of(vars));
  }

  /**
   * Constructs a variable list with the given variables.
   *
   * @param modifier modifier (CONST or VAR) for the variable list.
   * @param vars     variables to put in the variable list.
   * @return a variable list with the given modifier and variables.
   */
  public VariableList list(VariableModifier modifier, Collection<? extends Variable> vars) {
    final var result = chartFac.createVariableList();
    result.setModifier(modifier);
    result.getVars().addAll(vars);
    return result;
  }

  /**
   * Constructs a variable with the given name, type, and initial value.
   *
   * @param name    name of the variable.
   * @param type    type of the variable.
   * @param initial initial value expression of the variable.
   * @return a variable with the given name, type, and initial value expression.
   */
  public Variable var(String name, Type type, Expression initial) {
    final var result = var(name, type);
    result.setInitial(initial);
    return result;
  }

  /**
   * Constructs a variable with the given name and type (and no initial value).
   *
   * @param name name of the variable.
   * @param type type of the variable.
   * @return a variable with the given name and type.
   */
  public Variable var(String name, Type type) {
    final var result = chartFac.createVariable();
    result.setName(name);
    result.setType(type);
    return result;
  }
}
