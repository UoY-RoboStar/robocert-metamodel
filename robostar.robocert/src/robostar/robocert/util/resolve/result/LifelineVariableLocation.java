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
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;

/**
 * A variable location that is located within a lifeline.
 *
 * @param line lifeline location.
 */
public record LifelineVariableLocation(Lifeline line) implements VariableLocation {

  @Override
  public Optional<Interaction> interaction() {
    return Optional.of(line.getParent());
  }

  @Override
  public Optional<Lifeline> lifeline() {
    return Optional.of(line);
  }

  @Override
  public boolean isForActor(Actor a) {
    return EcoreUtil.equals(a, line.getActor());
  }
}
