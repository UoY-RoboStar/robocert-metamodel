/*
 * Copyright (c) 2021-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.tests.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import robostar.robocert.RoboCertFactory;
import robostar.robocert.Gate;

/**
 * Tests the string representation for {@link Gate}s, and also tests that the factory resolves them
 * correctly.
 *
 * @author Matt Windsor
 */
class GateImplTest {

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;

  /**
   * Tests that stringifying a world works as expected.
   */
  @Test
  void testToString() {
    final var gate = certFac.createGate();
    assertThat(gate.toString(), is(equalTo("<<gate>>")));
  }
}
