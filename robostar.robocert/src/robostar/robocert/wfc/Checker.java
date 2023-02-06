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

/**
 * A well-formedness checker.
 *
 * @param <T> the type of elements being checked for well-formedness.
 * @author Matt Windsor
 */
public interface Checker<T> {

  /**
   * Checks well-formedness of a given element.
   *
   * @param elem the element to check.
   * @return whether all the checks in this checker pass for {@code elem}
   */
  boolean isValid(T elem);
}
