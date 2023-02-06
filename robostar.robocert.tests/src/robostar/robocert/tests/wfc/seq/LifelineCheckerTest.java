/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.wfc.seq;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.GroupFinder;
import robostar.robocert.util.factory.robochart.ActorFactory;
import robostar.robocert.wfc.seq.LifelineChecker;

/**
 * Tests {@link LifelineChecker}.
 *
 * @author Matt Windsor
 */
class LifelineCheckerTest {
  private final LifelineChecker checker = new LifelineChecker(new GroupFinder());
  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;

  /**
   * An actor that is on the specification group.
   */
  private Actor actor;

  private Interaction seq;

  @BeforeEach
  void setUp() {
    actor = actFac.targetActor("A");

    final var sg = certFac.createSpecificationGroup();
    sg.getActors().add(actor);
    sg.setName("Grp");

    seq = certFac.createInteraction();
    seq.setGroup(sg);
    seq.setName("Seq");
  }

  /**
   * Tests that a lifeline on the prefab sequence with the prefab actor is well-formed.
   */
  @Test
  void testIsSLA1_WellFormed() {
    final var line = actFac.lifeline(actor);
    line.setParent(seq);

    assertTrue(checker.isSLA1(line), "actor is valid and should be SLA1");
  }

  /**
   * Tests that a lifeline with the prefab actor but not on the prefab sequence is ill-formed.
   */
  @Test
  void testIsSLA1_IllFormed_NoGroup() {
    final var line = actFac.lifeline(actor);

    assertFalse(checker.isSLA1(line), "actor with no sequence should not be SLA1");
  }

  /**
   * Tests that a lifeline on the prefab sequence but with an out-of-group actor is ill-formed.
   */
  @Test
  void testIsSLA1_IllFormed_BadActor() {

    final var line = actFac.lifeline(actFac.targetActor("B"));
    line.setParent(seq);

    assertFalse(checker.isSLA1(line), "lifeline with out-of-group actor should not be SLA1");
  }

}
