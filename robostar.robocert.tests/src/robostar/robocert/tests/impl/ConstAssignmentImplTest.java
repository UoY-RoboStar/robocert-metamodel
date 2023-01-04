/*
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
 */
package robostar.robocert.tests.impl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import robostar.robocert.ConstAssignment;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.robochart.ExpressionFactory;

/**
 * Tests any derived functionality on {@link ConstAssignment}s, and also tests that the factory
 * resolves it correctly.
 *
 * @author Matt Windsor
 */
class ConstAssignmentImplTest {

  private final ExpressionFactory ef = new ExpressionFactory(RoboChartFactory.eINSTANCE);
  protected RoboCertFactory rf = RoboCertFactory.eINSTANCE;
  private final RoboChartFactory cf = RoboChartFactory.eINSTANCE;

  private Variable x;
  private ConstAssignment asst;

  @BeforeEach
  void setUp() {
    x = cf.createVariable();
    x.setName("foo");

    asst = rf.createConstAssignment();
    asst.getConstants().add(x);
    asst.setValue(ef.integer(4));
  }

  /**
   * Tests {@code hasConstant} on a basic assignment.
   */
  @Test
  void testHasConstant_Basic() {
    assertThat(asst.hasConstant(x), is(true));

    final var y = cf.createVariable();
    y.setName("bar");
    assertThat(asst.hasConstant(y), is(false));
  }

  /**
   * Tests {@code hasConstant} handles variables with the same name but different parents
   * appropriately.
   */
  @Test
  void testHasConstant_SameNameDifferentParents() {
    final var y = EcoreUtil.copy(x);

    // wire x and y up to different parents
    makeParent(x, "X");
    makeParent(y, "Y");

    assertThat(asst.hasConstant(x), is(true));
    assertThat(asst.hasConstant(y), is(false));

    asst.getConstants().add(y);
    assertThat(asst.hasConstant(x), is(true));
    assertThat(asst.hasConstant(y), is(true));
  }

  private void makeParent(Variable v, String name) {
    final var par = cf.createControllerDef();
    par.setName(name);
    final var vars = cf.createVariableList();
    vars.getVars().add(v);
    vars.setModifier(VariableModifier.CONST);
    par.getVariableList().add(vars);
  }

}
