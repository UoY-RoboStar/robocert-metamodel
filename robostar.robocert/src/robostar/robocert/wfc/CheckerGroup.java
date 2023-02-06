/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.wfc;

import java.util.stream.Stream;

/**
 * Implements a {@link Checker} composing multiple other {@link Checker}s.
 *
 * @param <T> the type of element being checked.
 */
public abstract class CheckerGroup<T> implements Checker<T> {

  @Override
  public boolean isValid(T elem) {
    return checks().allMatch(c -> c.isValid(elem));
  }

  /**
   * @return the checks to be considered in this group.
   */
  protected abstract Stream<Checker<T>> checks();
}
