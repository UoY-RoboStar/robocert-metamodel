/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import robostar.robocert.util.factory.robochart.ExpressionFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.factory.robochart.VariableFactory;
import robostar.robocert.util.resolve.ExpressionVariableReferenceResolver;

/**
 * Tests expression variable reference resolution.
 *
 * @author Matt Windsor
 */
class ExpressionVariableReferenceResolverTest {

  private final ExpressionFactory exprFac = new ExpressionFactory(RoboChartFactory.eINSTANCE);

  private final TypeFactory tyFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private final VariableFactory varFac = VariableFactory.DEFAULT;

  private final ExpressionVariableReferenceResolver resolver = new ExpressionVariableReferenceResolver();

  /**
   * Tests {@code variablesReferencedBy} on various expressions.
   */
  @Test
  void TestVariablesReferencedBy() {
    final var var1 = varFac.var("x", tyFac.primRef("nat"));
    final var var2 = varFac.var("y", tyFac.primRef("real"));

    final var exp1 = exprFac.integer(42);
    final var exp2 = exprFac.ref(var1);
    final var exp3 = exprFac.ref(var2);
    final var exp4 = exprFac.plus(exp1, exp2);
    final var exp5 = exprFac.mult(exp4, exp3);

    assertThat(exp1, hasVariables());
    assertThat(exp2, hasVariables(var1));
    assertThat(exp3, hasVariables(var2));
    assertThat(exp4, hasVariables(var1));
    assertThat(exp5, hasVariables(var1, var2));
  }

  private Matcher<Expression> hasVariables(Variable... xs) {
    return new CustomTypeSafeMatcher<>("has the given referenced variables") {
      @Override
      protected boolean matchesSafely(Expression e) {
        return matcher().matches(resolver.variablesReferencedBy(e).toList());
      }

      @Override
      protected void describeMismatchSafely(Expression e, Description mismatchDescription) {
        matcher().describeMismatch(resolver.variablesReferencedBy(e).toList(), mismatchDescription);
      }

      private Matcher<? extends Iterable<? extends Variable>> matcher() {
        return xs.length == 0 ? empty() : contains(xs);
      }
    };
  }
}
