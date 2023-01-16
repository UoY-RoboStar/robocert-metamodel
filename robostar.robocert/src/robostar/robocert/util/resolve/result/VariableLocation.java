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
 * Information about the location of a resolved variable.
 */
public interface VariableLocation {

  /**
   * @return the interaction to which this variable belongs, if any.
   */
  Optional<Interaction> interaction();

  /**
   * @return the lifeline to which this variable belongs, if any.
   */
  Optional<Lifeline> lifeline();

  /**
   * Gets whether this result is over a lifeline representing the given actor.
   *
   * @param a actor in question.
   * @return whether the result names a lifeline and that lifeline's nested actor is equal to the
   * given actor.
   */
  boolean isForActor(Actor a);
}
