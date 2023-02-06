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

import com.google.inject.Inject;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.Actor;
import robostar.robocert.Lifeline;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.wfc.Checker;
import robostar.robocert.wfc.CheckerGroup;

/**
 * Checks well-formedness of lifelines.
 *
 * <p>
 * This implements all checks with code {@code SL}.
 *
 * @author Matt Windsor
 */
public class LifelineChecker extends CheckerGroup<Lifeline> {

  private final GroupFinder groupFinder;

  @Inject
  public LifelineChecker(GroupFinder gf) {
    Objects.requireNonNull(gf);
    groupFinder = gf;
  }

  @Override
  protected Stream<Checker<Lifeline>> checks() {
    return Stream.of(this::isSLA1);
  }

  /**
   * Checks whether the given lifeline satisfies {@code SLA1}.
   *
   * <p>
   * {@code SLA1} is 'The actor of a Lifeline must be present on the parent SpecificationGroup.'
   *
   * @param l the lifeline to check
   * @return whether {@code l} is {@code SLA1} compliant
   */
  public boolean isSLA1(Lifeline l) {
    final Predicate<Actor> isActorOfL = a -> EcoreUtil.equals(l.getActor(), a);

    final var group = groupFinder.find(l);
    final var actors = group.stream().flatMap(this::groupActors);
    return actors.anyMatch(isActorOfL);
  }

  private Stream<Actor> groupActors(SpecificationGroup g) {
    return g.getActors().stream();
  }
}
