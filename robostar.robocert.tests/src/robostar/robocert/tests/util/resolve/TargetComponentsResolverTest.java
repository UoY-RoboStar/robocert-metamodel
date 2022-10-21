/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */
package robostar.robocert.tests.util.resolve;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.ExpressionFactory;
import robostar.robocert.util.TargetFactory;
import robostar.robocert.util.resolve.DefinitionResolver;
import robostar.robocert.util.resolve.TargetComponentsResolver;

/**
 * Tests the {@link TargetComponentsResolver}.
 *
 * @author Matt Windsor
 */
class TargetComponentsResolverTest {

  private final ExpressionFactory exprFactory = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  private final RoboCertFactory certFactory = RoboCertFactory.eINSTANCE;
  private final RoboChartFactory chartFactory = RoboChartFactory.eINSTANCE;
  private final TargetFactory tgtFactory = new TargetFactory(RoboCertFactory.eINSTANCE);

  private final TargetComponentsResolver compRes = new TargetComponentsResolver(new DefinitionResolver());

  @BeforeEach
  void setUp() {
    // ?
  }

  /**
   * Tests {@code has} on a basic assignment.
   */
  @Test
  void testHasComponent_InController() {
    final var ctrl = chartFactory.createControllerDef();
    final var target = tgtFactory.inController(ctrl);

    // Now set up some state machines:
    final var stm1 = chartFactory.createStateMachineDef();
    stm1.setName("stm1");

    final var stm2 = chartFactory.createStateMachineDef();
    stm1.setName("stm2");

    // Neither of these should be in the controller yet:
    assertThat(compRes.hasComponent(target, stm1), is(false));
    assertThat(compRes.hasComponent(target, stm2), is(false));

    // Add in stm1 directly, and we should see stm1 go to true, but stm2 remain false:
    ctrl.getMachines().add(stm1);
    assertThat(compRes.hasComponent(target, stm1), is(true));
    assertThat(compRes.hasComponent(target, stm2), is(false));

    // If we add stm2 through a reference, it should still become a component of the controller:
    final var ref = chartFactory.createStateMachineRef();
    ref.setRef(stm2);
    ctrl.getMachines().add(ref);
    assertThat(compRes.hasComponent(target, stm1), is(true));
    assertThat(compRes.hasComponent(target, stm2), is(true));
    assertThat(compRes.hasComponent(target, ref), is(true));
  }

}
