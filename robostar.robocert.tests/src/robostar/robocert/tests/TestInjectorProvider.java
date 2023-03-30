/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests;

import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.impl.RoboChartFactoryImplCustom;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import robostar.robocert.util.RoboCertBaseModule;

/**
 * Provides a test injector for RoboCert.
 *
 * <p>
 * This injector exists because we don't have access to most of the Xtext infrastructure in the
 * metamodel class.  For Xtext-derived plugins, one should use the more idiomatic ways to get
 * injectors.
 */
public class TestInjectorProvider {

  /**
   * @return an injector that provides bindings suitable for tests.
   */
  public static Injector getInjector() {
    return Guice.createInjector(new RoboCertBaseModule(), new AbstractModule() {
      @Override
      protected void configure() {
        // We can't use the eINSTANCE here, because, without the Xtext boilerplate, it never picks
        // up the custom bindings.
        bind(RoboChartFactory.class).to(RoboChartFactoryImplCustom.class);
      }
    });
  }
}
