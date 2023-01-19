/*
 * Copyright (c) 2022, 2023 University of York and others
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

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.VariableModifier;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;
import robostar.robocert.util.factory.robochart.VariableFactory;
import robostar.robocert.util.resolve.VariableResolver;
import robostar.robocert.util.resolve.result.LifelineVariableLocation;
import robostar.robocert.util.resolve.result.NoVariableLocation;
import robostar.robocert.util.resolve.result.ResolvedVariable;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Tests {@link VariableResolver}
 */
class VariableResolverTest {

  private final VariableResolver resolver = new VariableResolver();

  private final RoboCertFactory certFac = RoboCertFactory.eINSTANCE;
  private final TypeFactory typeFac = new TypeFactory(RoboChartFactory.eINSTANCE);
  private final VariableFactory varFac = new VariableFactory(RoboChartFactory.eINSTANCE);

  private Lifeline line1;
  private Lifeline line2;

  private Interaction seq;

  private Actor wa;

  private ResolvedVariable wr;
  private ResolvedVariable xr;
  private ResolvedVariable yr;
  private ResolvedVariable zr;

  @BeforeEach
  void setUp() {
    final var type = typeFac.primRef("real");

    final var w = varFac.var("w", type);
    final var x = varFac.var("x", type);
    final var y = varFac.var("y", type);
    final var z = varFac.var("z", type);

    wa = certFac.createTargetActor();

    line1 = certFac.createLifeline();
    line1.setVariables(varFac.list(VariableModifier.VAR, w, x));
    line1.setActor(wa);

    line2 = certFac.createLifeline();
    line2.setVariables(varFac.list(VariableModifier.VAR, y, z));

    seq = certFac.createInteraction();
    seq.getLifelines().addAll(List.of(line1, line2));

    final var ll1 = new LifelineVariableLocation(line1);
    final var ll2 = new LifelineVariableLocation(line2);

    wr = new ResolvedVariable(w, ll1);
    xr = new ResolvedVariable(x, ll1);
    yr = new ResolvedVariable(y, ll2);
    zr = new ResolvedVariable(z, ll2);
  }

  /**
   * Checks that resolving behaves normally when everything is present.
   */
  @Test
  void testResolve_normal() {
    final var got = resolver.findAll(seq).toList();

    assertThat(got, hasItems(wr, xr, yr, zr));
  }

  /**
   * Checks that resolving ignores null variable lists.
   */
  @Test
  void testResolve_nullVarList() {
    line1.setVariables(null);

    final var got = resolver.findAll(seq).toList();
    assertThat(got, both(hasItems(yr, zr)).and(not(hasItem(wr))).and(not(hasItem(xr))));

    line2.setVariables(null);
    final var got2 = resolver.findAll(seq).toList();
    assertThat(got2, is(Collections.EMPTY_LIST));
  }

  /**
   * Checks that finding the lifeline of a variable inside a lifeline works properly.
   */
  @Test
  void testFindLifeline_inLifeline() {
    assertThat(resolver.resolve(wr.var()), is(wr));
    assertThat(resolver.resolve(xr.var()), is(xr));
    assertThat(resolver.resolve(yr.var()), is(yr));
    assertThat(resolver.resolve(zr.var()), is(zr));
  }

  /**
   * Checks that finding the lifeline of a variable not inside a lifeline works properly.
   */
  @Test
  void testFindLifeline_notInLifeline() {
    final var q = varFac.var("q", typeFac.primRef("real"));
    assertThat(resolver.resolve(q), is(new ResolvedVariable(q, new NoVariableLocation())));
  }

  /**
   * Tests {@code isForActor} works properly on results.
   */
  @Test
  void testIsForActor() {
    assertThat(wr.isForActor(wa), is(true));

    final var other = certFac.createComponentActor();
    assertThat(wr.isForActor(other), is(false));
  }
}
