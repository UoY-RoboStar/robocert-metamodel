/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.fragment;

import circus.robocalc.robochart.Variable;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import robostar.robocert.InteractionFragment;
import robostar.robocert.util.resolve.ExpressionVariableReferenceResolver;
import robostar.robocert.util.resolve.result.FragmentVariableSet;

/**
 * Helper for extracting non-recursive variable usage from an {@link InteractionFragment}.
 *
 * <p>
 * This class simplifies use of {@link FragmentBindingResolver} and
 * {@link FragmentExpressionResolver} when used together to deduce which variables form inputs when
 * entering and outputs when exiting a fragment.  This is useful, for instance, in the CSP
 * generator.
 *
 * @param exprVarRes the expression variable reference resolver
 * @param bindRes    the fragment binding (output variable) resolver
 * @param exprRes    the fragment expression resolver
 * @author Matt Windsor
 */
public record FragmentVariableResolver(ExpressionVariableReferenceResolver exprVarRes,
                                       FragmentBindingResolver bindRes,
                                       FragmentExpressionResolver exprRes) {

  @Inject
  public FragmentVariableResolver {
    Objects.requireNonNull(exprVarRes);
    Objects.requireNonNull(bindRes);
    Objects.requireNonNull(exprRes);
  }

  /**
   * Resolves the input and output variables of a fragment.
   *
   * @param fragment the fragment under scrutiny
   * @return a {@link FragmentVariableSet} containing the input and output variables of the given
   * fragment
   */
  public FragmentVariableSet variablesOf(InteractionFragment fragment) {
    return new FragmentVariableSet(inputs(fragment), outputs(fragment));
  }

  private Set<Variable> inputs(InteractionFragment fragment) {
    return exprRes.expressionsOf(fragment).flatMap(exprVarRes::variablesReferencedBy)
        .collect(Collectors.toUnmodifiableSet());
  }

  private Set<Variable> outputs(InteractionFragment fragment) {
    return bindRes.bindingsOf(fragment).collect(Collectors.toUnmodifiableSet());
  }
}
