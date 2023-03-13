/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.tests.matchers;

import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;

/**
 * Hamcrest matchers for RoboCert tests.
 *
 * @author Matt Windsor
 */
public class Matchers {

  /**
   * Constructs a resolves-to matcher with the given resolver and expected objects.
   *
   * @param resolver the resolver to test against
   * @param expected the object to test against
   * @param <I>      the type of inputs to the matcher
   * @param <O>      the type of outputs to the matcher
   */
  public static <I, O> ResolvesTo<I, O> resolvesTo(Function<I, Stream<O>> resolver, O[] expected) {
    return ResolvesTo.resolvesTo(resolver, expected);
  }

  /**
   * Constructs an is-structurally-equal matcher with the given expected object.
   *
   * @param expected the object to test against
   * @param <T>      the type of inputs to the matcher
   */
  public static <T extends EObject> IsStructurallyEqualTo<T> structurallyEqualTo(T expected) {
    return IsStructurallyEqualTo.structurallyEqualTo(expected);
  }
}
