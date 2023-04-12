/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory;

import circus.robocalc.robochart.NamedElement;
import java.util.function.Supplier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Base class for builders that mutably construct EMF objects.
 *
 * @param <Self> type of builder returned through chaining
 * @param <T>    type of item being built
 */
public abstract class AbstractEObjectBuilder<Self, T extends EObject> extends AbstractBuilder<T> {

  protected AbstractEObjectBuilder(T initial) {
    super(initial);
  }

  /**
   * Copies this builder.
   *
   * <p>
   * EObject builders are not immutable by default, so any attempt to reuse a builder requires
   * explicit copying.
   *
   * @return a deep copy of this builder
   */
  public Self copy() {
    return selfWith(EcoreUtil.copy(object));
  }

  /**
   * @return this, as a {@code Self}
   */
  protected abstract Self selfWith(T newObject);

  /**
   * @return this, as a {@code Self}
   */
  protected abstract Self self();

  /**
   * Helper for constructing initial objects whose only mandatory element is a name.
   *
   * @param factory the factory method for producing the object
   * @param name    the name to inject
   * @param <U>     the type of item being produced
   * @return the result of calling the factory then setting the given name
   */
  public static <U extends NamedElement> U makeInitialNamed(Supplier<U> factory, String name) {
    final var initial = factory.get();
    initial.setName(name);
    return initial;
  }
}
