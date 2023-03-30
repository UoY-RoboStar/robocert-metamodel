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

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.RoboChartFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.util.resolve.ControllerResolver;

/**
 * Tests the {@link ControllerResolver}.
 *
 * @author Matt Windsor
 */
class ControllerResolverTest {

  private final RoboChartFactory chartFactory = RoboChartFactory.eINSTANCE;

  private final ControllerResolver ctrlRes = new ControllerResolver();

  private RCModule mod;
  private RCPackage pkg;
  private ControllerDef cdef;
  private ControllerRef cref;

  @BeforeEach
  void setUp() {
    mod = chartFactory.createRCModule();
    mod.setName("Mod");

    pkg = chartFactory.createRCPackage();
    pkg.setName("Pkg");

    cdef = chartFactory.createControllerDef();
    cdef.setName("Ctrl");

    cref = chartFactory.createControllerRef();
    cref.setName("RCtrl");
  }

  /**
   * Tests {@code name} on a lone controller definition.
   */
  @Test
  void testName_LooseDef() {
    assertThat(ctrlRes.name(cdef), is(new String[]{"Ctrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a lone controller definition.
   */
  @Test
  void testUnqualifiedName_LooseDef() {
    assertThat(ctrlRes.unqualifiedName(cdef), is("Ctrl"));
  }

  /**
   * Tests {@code name} on a lone controller reference.
   */
  @Test
  void testName_LooseRef() {
    assertThat(ctrlRes.name(cref), is(new String[]{"RCtrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a lone controller reference.
   */
  @Test
  void testUnqualifiedName_LooseRef() {
    assertThat(ctrlRes.unqualifiedName(cref), is("RCtrl"));
  }

  /**
   * Tests {@code name} on a module-bound controller definition.
   */
  @Test
  void testName_DefInMod() {
    mod.getNodes().add(cdef);
    assertThat(ctrlRes.name(cdef), is(new String[]{"Mod", "Ctrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a module-bound controller definition.
   */
  @Test
  void testUnqualifiedName_DefInMod() {
    mod.getNodes().add(cdef);
    assertThat(ctrlRes.unqualifiedName(cdef), is("Ctrl"));
  }

  /**
   * Tests {@code name} on a module-bound controller reference.
   */
  @Test
  void testName_RefInMod() {
    // note that the definition is not in the module
    mod.getNodes().add(cref);
    assertThat(ctrlRes.name(cref), is(new String[]{"Mod", "RCtrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a module-bound controller reference.
   */
  @Test
  void testUnqualifiedName_RefInMod() {
    mod.getNodes().add(cref);
    assertThat(ctrlRes.unqualifiedName(cref), is("RCtrl"));
  }

  /**
   * Tests {@code name} on a package-bound controller definition.
   */
  @Test
  void testName_DefInPkg() {
    pkg.getControllers().add(cdef);
    assertThat(ctrlRes.name(cdef), is(new String[]{"Pkg", "Ctrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a package-bound controller definition.
   */
  @Test
  void testUnqualifiedName_DefInPkg() {
    pkg.getControllers().add(cdef);
    assertThat(ctrlRes.unqualifiedName(cdef), is("Ctrl"));
  }

  /**
   * Tests {@code name} on a module-bound controller reference where the definition is in a
   * package.
   */
  @Test
  void testName_RefInModDefInPkg() {
    // note that the definition is not in the module
    mod.getNodes().add(cref);
    pkg.getControllers().add(cdef);
    assertThat(ctrlRes.name(cref), is(new String[]{"Mod", "RCtrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a module-bound controller reference where the definition is in
   * a package.
   */
  @Test
  void testUnqualifiedName_RefInModDefInPkg() {
    // note that the definition is not in the module
    mod.getNodes().add(cref);
    pkg.getControllers().add(cdef);
    assertThat(ctrlRes.unqualifiedName(cref), is("RCtrl"));
  }

  /**
   * Tests {@code name} on a packaged-module-bound controller definition.
   */
  @Test
  void testName_DefInPkgMod() {
    pkg.getModules().add(mod);
    mod.getNodes().add(cdef);
    assertThat(ctrlRes.name(cdef), is(new String[]{"Pkg", "Mod", "Ctrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a packaged-module-bound controller definition.
   */
  @Test
  void testUnqualifiedName_DefInPkgMod() {
    pkg.getModules().add(mod);
    mod.getNodes().add(cdef);
    assertThat(ctrlRes.unqualifiedName(cdef), is("Ctrl"));
  }

  /**
   * Tests {@code name} on a packaged-module-bound controller reference.
   */
  @Test
  void testName_RefInPkgMod() {
    pkg.getModules().add(mod);
    mod.getNodes().add(cref);
    assertThat(ctrlRes.name(cref), is(new String[]{"Pkg", "Mod", "RCtrl"}));
  }

  /**
   * Tests {@code unqualifiedName} on a packaged-module-bound controller reference.
   */
  @Test
  void testUnqualifiedName_RefInPkgMod() {
    pkg.getModules().add(mod);
    mod.getNodes().add(cref);
    assertThat(ctrlRes.unqualifiedName(cref), is("RCtrl"));
  }

  /**
   * Tests that module resolution for controller definitions works properly when there is no
   * module.
   */
  @Test
  void testModule_Def_Absent() {
    final var result = ctrlRes.module(cdef);
    assertThat(result.isEmpty(), Matchers.is(true));
  }

  /**
   * Tests that module resolution for controller definitions works properly when there is no
   * module.
   */
  @Test
  void testModule_Ref_Absent() {
    final var result = ctrlRes.module(cref);
    assertThat(result.isEmpty(), Matchers.is(true));
  }

  /**
   * Tests that module resolution for controller definitions works properly.
   */
  @Test
  void testModule_Def_Present() {
    mod.getNodes().add(cdef);
    final var result = ctrlRes.module(cdef);
    assertThat(result.isPresent(), Matchers.is(true));
    assertThat(result.get(), Matchers.is(mod));
  }

  /**
   * Tests that module resolution for controller references works properly.
   */
  @Test
  void testModule_Ref_Present() {
    mod.getNodes().add(cref);
    final var result = ctrlRes.module(cref);
    assertThat(result.isPresent(), Matchers.is(true));
    assertThat(result.get(), Matchers.is(mod));
  }

  /**
   * Tests that module resolution for controllers with no module behaves as expected.
   */
  @Test
  void testModule_noModule() {
    final var c2 = chartFactory.createControllerDef();
    assertThat(ctrlRes.module(c2).isEmpty(), Matchers.is(true));
  }
}
