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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCModule;
import robostar.robocert.InModuleTarget;
import robostar.robocert.ModuleTarget;

/**
 * Tests basic resolution and stringifying functionality on {@link ModuleTarget}s.
 *
 * @author Matt Windsor
 */
class InModuleTargetImplTest extends TargetTest<InModuleTarget> {

  private Controller ctrl1;
  private Controller ctrl2;
  private RCModule module;

  @BeforeEach
  void setUp() {
    ctrl1 = rchartFactory.createControllerDef();
    ctrl1.setName("ctrl1");

    ctrl2 = rchartFactory.createControllerDef();
    ctrl2.setName("ctrl2");

    final var rp = rchartFactory.createRoboticPlatformDef();
    rp.setName("rp");

    module = rchartFactory.createRCModule();
    module.setName("foo");
    module.getNodes().addAll(List.of(ctrl1, ctrl2, rp));

    example = rcertFactory.createInModuleTarget();
    example.setModule(module);
  }

  @Override
  protected ConnectionNode[] expectedComponents() {
    return new ConnectionNode[]{ctrl1, ctrl2};
  }

  @Override
  protected NamedElement expectedElement() {
    return module;
  }

  @Override
  protected String expectedString() {
    return "components of module foo";
  }
}
