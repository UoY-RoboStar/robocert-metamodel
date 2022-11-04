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

import circus.robocalc.robochart.RCPackage;
import java.util.Optional;
import org.eclipse.emf.ecore.EObject;

/**
 * As-of-yet unsorted helper methods for resolution.
 *
 * <p>
 * Some of these duplicate functionality in EcoreUtil2, which is intentional; we don't want to pull
 * in a dependency on xtext here.
 *
 * @author Matt Windsor
 */
public class ResolveHelper {

  /**
   * Gets the package containing this object, if any.
   *
   * @param e object to inspect.
   * @return package of {@code e}, if any.
   */
  public static Optional<RCPackage> packageOf(EObject e) {
    return containerOfType(e, RCPackage.class);
  }

  /**
   * Gets the first container of the given object that matches the given class.
   * <p>
   * This should be compatible with {@code EcoreUtil2.getContainerOfType}.
   *
   * @param e      object to inspect.
   * @param tClass reification of T.
   * @param <T>    class of container desired.
   * @return the first container assignable from T.
   */
  public static <T> Optional<T> containerOfType(EObject e, Class<T> tClass) {
    for (var c = e.eContainer(); c != null; c = c.eContainer()) {
      if (tClass.isInstance(c)) {
        return Optional.of(tClass.cast(c));
      }
    }
    return Optional.empty();
  }
}
