/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.matchers;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher for checking if a resolver produces a given stream of outputs from an input.
 *
 * @param <I> the type of inputs to the matcher
 * @param <O> the type of outputs to the matcher
 */
public class ResolvesTo<I, O> extends TypeSafeMatcher<I> {

  private final Function<I, Stream<O>> resolver;
  private final O[] expected;

  public ResolvesTo(Function<I, Stream<O>> resolver, O[] expected) {
    this.resolver = resolver;
    this.expected = expected;
  }

  /**
   * Constructs a resolves-to matcher with the given resolver and expected objects.
   *
   * @param resolver the resolver to test against
   * @param expected the object to test against
   * @param <I>      the type of inputs to the matcher
   * @param <O>      the type of outputs to the matcher
   */
  public static <I, O> ResolvesTo<I, O> resolvesTo(Function<I, Stream<O>> resolver, O[] expected) {
    return new ResolvesTo<>(resolver, expected);
  }

  @Override
  protected boolean matchesSafely(I it) {
    return matcher().matches(resolve(it));
  }

  @Override
  protected void describeMismatchSafely(I it, Description mismatchDescription) {
    matcher().describeMismatch(resolve(it), mismatchDescription);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("resolves to ").appendValue(expected);
  }

  private Matcher<? extends Iterable<? extends O>> matcher() {
    return expected.length == 0 ? empty() : containsInAnyOrder(expected);
  }

  private List<EObject> resolve(I it) {
    return resolver.apply(it).map(EObject.class::cast).toList();
  }
}
