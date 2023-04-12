/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory;

import java.util.Objects;

/**
 * Base class for builders.
 *
 * @param <T> type of item being built
 */
public abstract class AbstractBuilder<T> {
  protected AbstractBuilder(T initial) {
    object = Objects.requireNonNull(initial, "initial object must not be null");
  }

  /**
   * The object being built.
   */
  protected final T object;

  /**
   * @return the object being built
   */
  public T get() {
    return object;
  }
}
