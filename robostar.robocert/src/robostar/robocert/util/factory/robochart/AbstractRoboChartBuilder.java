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
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.util.factory.AbstractEObjectBuilder;

public abstract class AbstractRoboChartBuilder<Self, T extends EObject> extends AbstractEObjectBuilder<Self, T> {
  protected final RoboChartFactory chartFactory;

  protected AbstractRoboChartBuilder(RoboChartFactory factory, T initial) {
    super(initial);
    chartFactory = Objects.requireNonNull(factory, "RoboChart factory must not be null");
  }
}
