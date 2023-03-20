/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.util.resolve.ControllerResolver;
import robostar.robocert.util.resolve.StateMachineResolver;

/**
 * Tests various aspects of {@link StateMachineResolver}.
 *
 * @author Matt Windsor
 */
class StateMachineResolverTest {
  private final RoboChartFactory chartFactory = RoboChartFactory.eINSTANCE;
  private final StateMachineResolver resolver = new StateMachineResolver(new ControllerResolver());

  private ControllerDef ctrl;
  private StateMachineDef stm;
  private OperationDef op;

  @BeforeEach
  void setUp() {
    final var mod = chartFactory.createRCModule();
    mod.setName("Mod");

    ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");
    mod.getNodes().add(ctrl);

    stm = chartFactory.createStateMachineDef();
    stm.setName("Stm");
    ctrl.getMachines().add(stm);

    op = chartFactory.createOperationDef();
    op.setName("Op");
    ctrl.getLOperations().add(op);
  }

  /**
   * Tests that name resolution for state machines and operators works properly.
   */
  @Test
  void testName() {
    // TODO(@MattWindsor91): stm/op not in mod/ctrl
    assertThat(resolver.name(stm), is(arrayContaining("Mod", "Ctrl", "Stm")));

    // Note that OP_ is not appended here.  That is a CSP concern.
    assertThat(resolver.name(op), is(arrayContaining("Mod", "Ctrl", "Op")));
  }

  /**
   * Tests that module resolution for controllers works properly.
   */
  @Test
  void testController() {
    final var result = resolver.controller(stm);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(ctrl));

    final var result2 = resolver.controller(op);
    assertThat(result2.isPresent(), is(true));
    assertThat(result2.get(), is(ctrl));
  }

  /**
   * Tests that module resolution for controllers with no module behaves as expected.
   */
  @Test
  void testController_noController() {
    final var s2 = chartFactory.createStateMachineDef();
    assertThat(resolver.controller(s2).isEmpty(), is(true));
    final var o2 = chartFactory.createOperationDef();
    assertThat(resolver.controller(o2).isEmpty(), is(true));
  }
}
