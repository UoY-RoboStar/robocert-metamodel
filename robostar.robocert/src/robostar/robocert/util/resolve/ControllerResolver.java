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

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RCPackage;
import com.google.common.collect.Streams;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Resolves various aspects of controllers.
 *
 * @author Matt Windsor
 */
public class ControllerResolver implements NameResolver<Controller> {

  /**
   * Gets the enclosing module for a RoboChart controller.
   * <p>
   * This assumes that the controller is inside a module.
   *
   * @param c the RoboChart controller.
   * @return the controller's module, if it has one.
   */
  public Optional<RCModule> module(Controller c) {
    return ResolveHelper.containerOfType(c, RCModule.class);
  }

  @Override
  public String[] name(Controller element) {
    final var pkg = ResolveHelper.packageOf(element).map(RCPackage::getName);
    final var mod = module(element).map(RCModule::getName);
    final var name = element.getName();

    return Streams.concat(pkg.stream(), mod.stream(), Stream.of(name)).toArray(String[]::new);
  }
}
