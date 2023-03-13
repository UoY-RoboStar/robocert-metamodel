/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;

import java.util.stream.Stream;

import robostar.robocert.util.StreamHelper;

/**
 * Extracts variable references from expressions.
 *
 * @author Matt Windsor
 */
public class ExpressionVariableReferenceResolver {

  /**
   * Gets all non-constant variables referenced by an expression.
   *
   * @param expr the expression to scrutinise.
   * @return the variables within the given expression.
   */
  public Stream<Variable> variablesReferencedBy(Expression expr) {
    final var self = StreamHelper.filter(Stream.of(expr), RefExp.class);
    final var contents = ResolveHelper.allContentsOfType(expr, RefExp.class);
    final var all = Stream.concat(self, contents);

    return all.mapMulti((r, p) -> {
      if (r.getRef() instanceof Variable v && v.getModifier() == VariableModifier.VAR) {
        p.accept(v);
      }
    });
  }
}
