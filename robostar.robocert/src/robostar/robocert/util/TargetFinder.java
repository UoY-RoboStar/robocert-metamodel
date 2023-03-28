/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util;

import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;

/**
 * Helper class for finding the target associated with an element.
 *
 * <p>This class prefers to use direct links within the metamodel where
 * possible, but will fall back to performing a general EMF container check if this isn't
 * available.
 *
 * @author Matt Windsor
 */
public class TargetFinder extends GroupElementFinder<Target> {

  private final GroupFinder groupFinder;

  @Inject
  public TargetFinder(GroupFinder groupFinder) {
    Objects.requireNonNull(groupFinder);
    this.groupFinder = groupFinder;
  }

  @Override
  public Optional<Target> findOnTarget(Target t) {
    return Optional.ofNullable(t);
  }

  @Override
  public Optional<Target> findOnGroup(SpecificationGroup g) {
    return groupFinder.findOnGroup(g).map(SpecificationGroup::getTarget)
        .flatMap(this::findOnTarget);
  }
}
