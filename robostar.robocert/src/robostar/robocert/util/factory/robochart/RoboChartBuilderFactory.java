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

import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.Type;
import java.util.Objects;
import javax.inject.Inject;

/**
 * Factory for producing RoboChart elements using the builder pattern rather than Xtend syntax or
 * direct field manipulation.
 *
 * @param chartFac the underlying RoboChart factory
 * @author Matt Windsor
 */
public record RoboChartBuilderFactory(RoboChartFactory chartFac) {

  @Inject
  public RoboChartBuilderFactory {
    Objects.requireNonNull(chartFac, "RoboChart factory must be non-null");
  }

  /**
   * Initiates a builder for a controller definition.
   *
   * @param name the name of the controller definition to build
   * @return a {@link ControllerDefBuilder} over the new definition
   */
  public ControllerDefBuilder controllerDef(String name) {
    return new ControllerDefBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for a RoboChart module.
   *
   * @param name the name of the RoboChart module to build
   * @param rp   the robotic platform (definition or reference)
   * @return a {@link RCModuleBuilder} over the new module
   */
  public RCModuleBuilder module(String name, RoboticPlatform rp) {
    return new RCModuleBuilder(chartFac, name, rp);
  }

  /**
   * Initiates a builder for an operation definition.
   *
   * @param name the name of the operation definition to build
   * @return a {@link OperationDefBuilder} over the new signature
   */
  public OperationDefBuilder operationDef(String name) {
    return new OperationDefBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for an operation signature.
   *
   * @param name the name of the operation signature to build
   * @return a {@link OperationSigBuilder} over the new signature
   */
  public OperationSigBuilder operationSig(String name) {
    return new OperationSigBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for a RoboChart package.
   *
   * @param name the name of the RoboChart package to build
   * @return a {@link RCPackageBuilder} over the new package
   */
  public RCPackageBuilder pkg(String name) {
    return new RCPackageBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for a robotic platform definition.
   *
   * @param name the name of the robotic platform definition to build
   * @return a {@link RoboticPlatformDefBuilder} over the new definition
   */
  public RoboticPlatformDefBuilder rpDef(String name) {
    return new RoboticPlatformDefBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for a state machine definition.
   *
   * @param name the name of the state machine definition to build
   * @return a {@link StateMachineDefBuilder} over the new definition
   */
  public StateMachineDefBuilder stmDef(String name) {
    return new StateMachineDefBuilder(chartFac, name);
  }

  /**
   * Initiates a builder for a variable.
   *
   * @param name the name of the variable to build
   * @param type the type of the variable to build
   * @return a {@link VariableBuilder} over the new definition
   */
  public VariableBuilder var(String name, Type type) {
    return new VariableBuilder(chartFac, name, type);
  }

  //
  // Not builder-constructed, but stored in this class because they are adjacent to things that are
  // builder-constructed:
  //

  /**
   * Constructs an operation parameter.
   * @param name the name of the parameter
   * @param type the type of the parameter
   * @return the parameter
   */
  public Parameter parameter(String name, Type type) {
    final var param = chartFac.createParameter();
    param.setName(name);
    param.setType(type);
    return param;
  }
}
