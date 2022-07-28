/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robostar.robocert.tests.impl;

import org.junit.jupiter.api.BeforeEach;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.StateMachineDef;
import robostar.robocert.StateMachineTarget;

/**
 * Tests basic resolution and stringifying functionality on {@link StateMachineTarget}s.
 *
 * @author Matt Windsor
 */
class StateMachineTargetImplTest extends TargetTest<StateMachineTarget> {

  private StateMachineDef def;

  @BeforeEach
  void setUp() {
    example = rcertFactory.createStateMachineTarget();

    def = rchartFactory.createStateMachineDef();
    def.setName("foo");
    example.setStateMachine(def);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {};
  }

  @Override
  protected NamedElement expectedElement() {
    return def;
  }

  @Override
  protected String expectedString() {
    return "state machine foo";
  }
}
