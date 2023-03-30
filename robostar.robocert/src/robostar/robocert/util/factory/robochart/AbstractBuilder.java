/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory.robochart;

import circus.robocalc.robochart.RoboChartFactory;

/**
 * Base class for builders.
 *
 * @param <T> type of item being built
 */
public abstract class AbstractBuilder<T> {
  /**
   * The object being built.
   */
  protected T object;

  /**
   * The underlying RoboChart factory.
   */
  protected RoboChartFactory chartFac;

  /**
   * @return the object being built
   */
  public T get() {
    return object;
  }
}
