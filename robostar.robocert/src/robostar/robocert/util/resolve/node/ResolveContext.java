/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.node;

import java.util.List;
import java.util.Objects;
import robostar.robocert.Actor;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;

/**
 * Common context shared across node and event resolvers.
 * @param target the target against which we are resolving
 * @param actors the list of all actors permitted in the current context
 */
public record ResolveContext(Target target, List<Actor> actors) {
  public ResolveContext {
    Objects.requireNonNull(target, "target must not be null");
    Objects.requireNonNull(actors, "actors must not be null");
  }

  /**
   * Constructs a resolution context from the target and actors of a group.
   * @param group the group from which we are creating the context
   */
  public ResolveContext(SpecificationGroup group) {
    this(group.getTarget(), group.getActors());
  }
}
