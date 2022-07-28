/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
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
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.StateMachineDef;
import robostar.robocert.InControllerTarget;

/**
 * Tests basic resolution and stringifying functionality on {@link InControllerTarget}s.
 *
 * @author Matt Windsor
 */
class InControllerTargetImplTest extends TargetTest<InControllerTarget> {

  private ControllerDef ctrl;
  private StateMachineDef stm;
  private OperationDef op;

  @BeforeEach
  void setUp() {
    stm = rchartFactory.createStateMachineDef();
    stm.setName("stm");

    op = rchartFactory.createOperationDef();
    op.setName("op");

    final var rp = rchartFactory.createRoboticPlatformDef();
    rp.setName("rp");

    ctrl = rchartFactory.createControllerDef();
    ctrl.setName("foo");
    ctrl.getMachines().add(stm);
    ctrl.getLOperations().add(op);

    example = rcertFactory.createInControllerTarget();
    example.setController(ctrl);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[] {stm, op};
  }

  @Override
  protected NamedElement expectedElement() {
    return ctrl;
  }

  @Override
  protected String expectedString() {
    return "components of controller foo";
  }
}
