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

import circus.robocalc.robochart.BasicContext;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.RoboChartFactory;
import java.util.List;

/**
 * Base class for builders that construct basic contexts.
 *
 * @param <Self> type of builder returned through chaining
 * @param <T>    type of item being built
 */
public abstract class AbstractBasicContextBuilder<Self, T extends BasicContext> extends
    AbstractRoboChartBuilder<Self, T> {

  protected AbstractBasicContextBuilder(RoboChartFactory factory, T initial) {
    super(factory, initial);
  }

  /**
   * Adds events to the context being built.
   *
   * @param events the events to add to the context
   * @return a reference to the current builder
   */
  public Self events(Event... events) {
    object.getEvents().addAll(List.of(events));

    return self();
  }

  /**
   * Adds operations to the context being built.
   *
   * @param operations the operations to add to the context
   * @return a reference to the current builder
   */
  public Self operations(OperationSig... operations) {
    object.getOperations().addAll(List.of(operations));

    return self();
  }

}
