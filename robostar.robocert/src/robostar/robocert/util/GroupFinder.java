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

import robostar.robocert.SpecificationGroup;

import java.util.Optional;

/**
 * Helper class for finding the parent specification group of an element.
 *
 * <p>This class prefers to use direct links within the metamodel where
 * possible, but will fall back to performing a general EMF container check if this isn't
 * available.
 *
 * @author Matt Windsor
 */
public class GroupFinder extends GroupElementFinder<SpecificationGroup> {

  @Override
  public Optional<SpecificationGroup> findOnGroup(SpecificationGroup g) {
    return Optional.ofNullable(g);
  }
}
