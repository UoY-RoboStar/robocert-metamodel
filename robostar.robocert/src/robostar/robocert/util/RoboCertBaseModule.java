/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util;

import com.google.inject.AbstractModule;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.resolve.message.EventResolver;
import robostar.robocert.util.resolve.message.EventResolverImpl;

/**
 * Guice module for injecting RoboCert dependencies.
 *
 * @author Matt Windsor
 */
public class RoboCertBaseModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(RoboCertFactory.class).toInstance(RoboCertFactory.eINSTANCE);
    bind(EventResolver.class).to(EventResolverImpl.class);
  }
}
