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
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.wfc.seq.InteractionChecker;

/**
 * Tests {@link InteractionChecker}.
 *
 * @author Matt Windsor
 */
class InteractionCheckerTest {

  private final InteractionChecker checker = new InteractionChecker();
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
   * Tests that an interaction with exactly one lifeline, matching the prefab actor, is valid.
   */
  @Test
  void testIsSIL1_WellFormed() {
    final var line = actFac.lifeline(actor);
    line.setParent(seq);

    assertTrue(checker.isSIL1(seq), "sequence is valid and should be SIL1");
  }

  /**
   * Tests that an interaction with no lifelines is invalid according to SIL1.
   */
  @Test
  void testIsSIL1_IllFormed_NoLifeline() {
    assertFalse(checker.isSIL1(seq), "sequence is missing a lifeline, shouldn't be SIL1");
  }

  /**
   * Tests that an interaction with duplicate lifelines is invalid according to SIL1.
   */
  @Test
  void testIsSIL1_IllFormed_DuplicateLifeline() {
    final var line1 = actFac.lifeline(actor);
    line1.setParent(seq);
    final var line2 = actFac.lifeline(actor);
    line2.setParent(seq);

    assertFalse(checker.isSIL1(seq), "sequence has duplicate lifelines, shouldn't be SIL1");
  }

  /**
   * Tests that a lifeline on the prefab sequence but with an out-of-group actor is ill-formed.
   */
  @Test
  void testIsSIL1_IllFormed_BadActor() {

    final var line = actFac.lifeline(actFac.targetActor("B"));
    line.setParent(seq);

    assertFalse(checker.isSIL1(seq), "sequence with out-of-group actor shouldn't be SIL1");
  }

}
