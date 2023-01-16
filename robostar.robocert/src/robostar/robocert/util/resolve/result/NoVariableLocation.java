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

import java.util.Optional;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;

/**
 * Null object for variable locations.
 *
 * @author Matt Windsor
 */
public class NoVariableLocation implements VariableLocation {

  @Override
  public Optional<Interaction> interaction() {
    return Optional.empty();
  }

  @Override
  public Optional<Lifeline> lifeline() {
    return Optional.empty();
  }

  @Override
  public boolean isForActor(Actor a) {
    return false;
  }

  @Override
  public String toString() {
    return "(no location)";
  }

  @Override
  public boolean equals(Object obj) {
    // All NoVariableLocations are equal.
    return obj instanceof NoVariableLocation;
  }
}
