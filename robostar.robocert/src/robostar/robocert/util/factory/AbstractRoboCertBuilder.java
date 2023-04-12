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
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.RoboCertFactory;

/**
 * Base class for builders over RoboCert objects, using the RoboCert factory.
 *
 * @param <Self> the type of this builder
 * @param <T> the type of object being built
 * @author Matt Windsor
 */
public abstract class AbstractRoboCertBuilder<Self, T extends EObject> extends AbstractEObjectBuilder<Self, T> {
  protected final RoboCertFactory certFactory;

  protected AbstractRoboCertBuilder(RoboCertFactory factory, T initial) {
    super(initial);
    certFactory = Objects.requireNonNull(factory, "RoboCert factory must not be null");
  }
}
