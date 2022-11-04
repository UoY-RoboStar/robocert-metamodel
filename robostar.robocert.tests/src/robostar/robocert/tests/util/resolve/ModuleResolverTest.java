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

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboChartFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.util.resolve.DefinitionResolver;
import robostar.robocert.util.resolve.ModuleResolver;

/**
 * Tests the {@link ModuleResolver}.
 *
 * @author Matt Windsor
 */
class ModuleResolverTest {

  private final RoboChartFactory chartFactory = RoboChartFactory.eINSTANCE;

  private final ModuleResolver modRes = new ModuleResolver(new DefinitionResolver());

  private RCModule mod;

  @BeforeEach
  void setUp() {
    mod = chartFactory.createRCModule();
    mod.setName("Mod");
  }

  /**
   * Tests {@code name} on a module with no package.
   */
  @Test
  void testName_Unpackaged() {
    assertThat(modRes.name(mod), is(new String[]{"Mod"}));
  }

  /**
   * Tests {@code name} on a module with a package.
   */
  @Test
  void testName_Packaged() {
    final var pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");
    pkg.getModules().add(mod);

    assertThat(modRes.name(mod), is(new String[]{"Pkg", "Mod"}));
  }
}
