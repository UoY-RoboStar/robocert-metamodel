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

import org.junit.jupiter.api.BeforeEach;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCModule;
import robostar.robocert.ModuleTarget;

/**
 * Tests basic resolution and stringifying functionality on {@link ModuleTarget}s.
 *
 * @author Matt Windsor
 */
class ModuleTargetImplTest extends TargetTest<ModuleTarget> {

  private RCModule module;

  @BeforeEach
  void setUp() {
    module = rchartFactory.createRCModule();
    module.setName("foo");

    example = rcertFactory.createModuleTarget();
    example.setModule(module);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[]{};
  }

  @Override
  protected NamedElement expectedElement() {
    return module;
  }

  @Override
  protected String expectedString() {
    return "module foo";
  }
}
