/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.message;

import circus.robocalc.robochart.Variable;
import java.util.Objects;

/**
 * Pairs a variable with the index of an argument binding it in a message argument list.
 *
 * @param index the index of the binding in the argument list
 * @param variable the variable being bound
 *
 * @author Matt Windsor
 */
public record IndexedVariableBinding(long index, Variable variable) {
  public IndexedVariableBinding {
    if (index < 0) {
      throw new IndexOutOfBoundsException("negative argument index");
    }
    Objects.requireNonNull(variable, "variable may not be null");
  }
}
