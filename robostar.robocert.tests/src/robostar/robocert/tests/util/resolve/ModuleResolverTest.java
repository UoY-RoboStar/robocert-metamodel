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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.google.inject.Guice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.tests.examples.MessageResolveExample;
import robostar.robocert.util.RoboCertBaseModule;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.resolve.ModuleResolver;

/**
 * Tests the {@link ModuleResolver}.
 *
 * @author Matt Windsor
 */
class ModuleResolverTest {

  private RoboChartBuilderFactory chartFac;

  private ModuleResolver modRes;

  private MessageResolveExample msgResolveExample;

  @BeforeEach
  void setUp() {
    final var inj = Guice.createInjector(new RoboCertBaseModule());
    chartFac = inj.getInstance(RoboChartBuilderFactory.class);
    modRes = inj.getInstance(ModuleResolver.class);
    msgResolveExample = inj.getInstance(MessageResolveExample.class);
  }

  /**
   * Tests {@code name} on a module with no package.
   */
  @Test
  void testName_Unpackaged() {
    final var mod = chartFac.module("Mod", msgResolveExample.rp).get();
    assertThat(modRes.name(mod), is(new String[]{"Mod"}));
  }

  /**
   * Tests {@code name} on a module with a package.
   */
  @Test
  void testName_Packaged() {
    final var mod = chartFac.module("Mod", msgResolveExample.rp).get();
    final var ignoredPkg = chartFac.pkg("Pkg").modules(mod).get();

    assertThat(modRes.name(mod), is(new String[]{"Pkg", "Mod"}));
  }

  /**
   * Tests {@code outboundConnections} on the toy example.
   */
  @Test
  void testOutboundConnections() {
    final var conns = modRes.outboundConnections(msgResolveExample.target.getModule()).toList();

    assertThat(conns, containsInAnyOrder(msgResolveExample.conn));
  }
}
