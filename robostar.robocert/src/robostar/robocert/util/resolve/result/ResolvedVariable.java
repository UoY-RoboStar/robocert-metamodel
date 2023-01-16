/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.result;

import circus.robocalc.robochart.Variable;
import robostar.robocert.Actor;

/**
 * Wraps a resolved variable with all information gathered about it during resolution.
 *
 * @param var      variable.
 * @param location location of the variable.
 */
public record ResolvedVariable(Variable var, VariableLocation location) {

  /**
   * Gets whether this variable is inside a lifeline representing the given actor.
   *
   * @param a actor in question.
   * @return whether the variable location names a lifeline and that lifeline's nested actor is
   * equal to the given actor.
   */
  public boolean isForActor(Actor a) {
    return location.isForActor(a);
  }
}
