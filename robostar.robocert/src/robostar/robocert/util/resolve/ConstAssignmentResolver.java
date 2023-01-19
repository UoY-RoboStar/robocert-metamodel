/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.util.resolve;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Variable;
import robostar.robocert.ConstAssignment;

/**
 * Helpers for resolving variables and instantiations within ConstAssignments.
 */
public class ConstAssignmentResolver {

  /**
   * Gets a stream of all constants instantiated by this instantiation.
   *
   * <p>
   * This stream is not deduplicated; we assume that either the instantiation is well-formed (no
   * multiple assignments), or that any deduplication happens later on.
   *
   * @param assignments the list of assignments to inspect (can be null)
   * @return the stream of constants in inst
   */
  public Stream<Variable> constantsOf(List<ConstAssignment> assignments) {
    return streamOf(assignments).flatMap(x -> x.getConstants().stream());
  }

  /**
   * Tries to get the instantiation of a constant within an assignment list.
   *
   * @param assignments the list of assignments to inspect (can be null)
   * @param k           the constant to find
   * @return the expression corresponding to k, if it is instantiated in inst
   */
  public Optional<Expression> instantiationOf(List<ConstAssignment> assignments, Variable k) {
    // TODO(@MattWindsor91): do we need to take Parameter here instead?
    return streamOf(assignments).mapMulti((ConstAssignment x, Consumer<Expression> acc) -> {
      if (x.hasConstant(k)) {
        acc.accept(x.getValue());
      }
    }).findFirst();
  }

  private Stream<ConstAssignment> streamOf(List<ConstAssignment> inst) {
    return Stream.ofNullable(inst).flatMap(List::stream);
  }
}
