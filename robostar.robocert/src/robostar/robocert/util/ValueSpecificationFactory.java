/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robostar.robocert.util;

import com.google.inject.Inject;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.WildcardValueSpecification;

/**
 * High-level factory for creating value specifications (arguments).
 *
 * @author Matt Windsor
 */
public record ValueSpecificationFactory(ExpressionFactory exprFactory, RoboChartFactory rchartFactory, RoboCertFactory rcertFactory) {

  /**
   * Constructs a value specification factory.
   * @param exprFactory the expression factory to which we delegate.
   * @param rcertFactory the low-level RoboCert factory to which we delegate.
   */
  @Inject
  public ValueSpecificationFactory {}

  /**
   * Constructs a value specification for the given integer.
   *
   * @param v the integer to lift to a value specification.
   * @return the given integer value specification.
   */
  public ExpressionValueSpecification integer(int v) {
    final var spec = rcertFactory.createExpressionValueSpecification();
    spec.setExpr(exprFactory.integer(v));
    return spec;
  }

  /**
   * Constructs an unbound wildcard value specification.
   *
   * @return  a wildcard value specification.
   */
  public WildcardValueSpecification wildcard() {
    return rcertFactory.createWildcardValueSpecification();
  }

  /**
   * Constructs a bound value specification.
   *
   * @param bnd the binding.
   *
   * @return  a bound value specification.
   */
  public WildcardValueSpecification bound(Variable bnd) {
    final var spec = rcertFactory.createWildcardValueSpecification();
    spec.setDestination(bnd);
    return spec;
  }

  /**
   * Constructs a throwaway binding (generally useful for testing only).
   *
   * @param name the name of the binding.
   *
   * @return the binding.
   */
  public Variable binding(String name) {
    final var bnd = rchartFactory.createVariable();
    bnd.setName(name);
    bnd.setType(rchartFactory.createAnyType());
    return bnd;
  }
}
