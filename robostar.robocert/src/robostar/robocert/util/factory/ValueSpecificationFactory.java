/*
 * Copyright (c) 2022, 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory;

import circus.robocalc.robochart.Expression;
import com.google.inject.Inject;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.WildcardValueSpecification;
import robostar.robocert.util.factory.robochart.ExpressionFactory;

/**
 * High-level factory for creating value specifications (arguments).
 *
 * @author Matt Windsor
 */
public record ValueSpecificationFactory(ExpressionFactory exprFactory,
                                        RoboChartFactory rchartFactory,
                                        RoboCertFactory rcertFactory) {

  /**
   * Constructs a value specification factory.
   *
   * @param exprFactory  the expression factory to which we delegate.
   * @param rcertFactory the low-level RoboCert factory to which we delegate.
   */
  @Inject
  public ValueSpecificationFactory {
  }

  /**
   * Constructs a value specification for the given integer.
   *
   * @param v the integer to lift to a value specification.
   * @return the given integer value specification.
   */
  public ExpressionValueSpecification integer(int v) {
    return expr(exprFactory.integer(v));
  }

  /**
   * Constructs a value specification for the given variable.
   *
   * @param v the variable to lift to a value specification.
   * @return the given variable value specification.
   */
  public ExpressionValueSpecification var(Variable v) {
    return expr(exprFactory.ref(v));
  }

  /**
   * Constructs a value specification for the given expression.
   *
   * @param expr the expression to lift to a value specification.
   * @return the given expression value specification.
   */
  public ExpressionValueSpecification expr(Expression expr) {
    final var spec = rcertFactory.createExpressionValueSpecification();
    spec.setExpr(expr);
    return spec;
  }

  /**
   * Constructs an unbound wildcard value specification.
   *
   * @return a wildcard value specification.
   */
  public WildcardValueSpecification wildcard() {
    return rcertFactory.createWildcardValueSpecification();
  }

  /**
   * Constructs a bound value specification.
   *
   * @param bnd the binding.
   * @return a bound value specification.
   */
  public WildcardValueSpecification bound(Variable bnd) {
    final var spec = wildcard();
    spec.setDestination(bnd);
    return spec;
  }
}
