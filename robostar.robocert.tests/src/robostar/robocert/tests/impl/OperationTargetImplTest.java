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
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.OperationDef;
import robostar.robocert.OperationTarget;

/**
 * Tests basic resolution and stringifying functionality on {@link OperationTarget}s.
 *
 * @author Matt Windsor
 */
class OperationTargetImplTest extends TargetTest<OperationTarget> {

  private OperationDef def;

  @BeforeEach
  void setUp() {
    example = rcertFactory.createOperationTarget();

    def = rchartFactory.createOperationDef();
    def.setName("foo");
    example.setOperation(def);
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
    return "operation foo";
  }
}
