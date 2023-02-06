/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.wfc.seq;

import java.util.List;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;
import robostar.robocert.wfc.Checker;
import robostar.robocert.wfc.CheckerGroup;

/**
 * Checks well-formedness of interactions.
 *
 * <p>
 * This implements all checks with code {@code SI}.
 *
 * @author Matt Windsor
 */
public class InteractionChecker extends CheckerGroup<Interaction> {

  @Override
  protected Stream<Checker<Interaction>> checks() {
    return Stream.of(this::isSIL1);
  }

  // TODO: SIV1

  /**
   * Checks whether the given interaction satisfies {@code SIL1}.
   *
   * <p>
   * {@code SIL1} is 'The lifelines of an Interaction must contain each of the actors of the parent
   * SpecificationGroup (as an actor) exactly once'.
   *
   * @param seq the interaction to check
   * @return whether {@code seq} is {@code SLA1} compliant
   */
  public boolean isSIL1(Interaction seq) {
    final var group = seq.getGroup();
    final var actors = group.getActors();
    // Don't de-duplicate this yet, we want to check how many times each actor is mentioned.
    final var lines = seq.getLifelines();

    final var counts = actors.stream()
        .map(a -> countActorInLifelines(lines, a));
    return counts.allMatch(c -> c == 1);
  }

  private static long countActorInLifelines(List<Lifeline> lines, Actor a) {
    return lines.stream().filter(l -> EcoreUtil.equals(a, l.getActor())).count();
  }
}
