/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.util.resolve.fragment;

import static org.hamcrest.MatcherAssert.assertThat;

import circus.robocalc.robochart.Variable;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.InteractionFragment;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.tests.examples.FragmentExample;
import robostar.robocert.tests.matchers.Matchers;
import robostar.robocert.util.resolve.fragment.FragmentBindingResolver;

/**
 * Tests fragment binding resolution.
 *
 * @author Matt Windsor
 */
class FragmentBindingResolverTest {

  private FragmentBindingResolver resolver;
  private FragmentExample example;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();
    resolver = inj.getInstance(FragmentBindingResolver.class);
    example = inj.getInstance(FragmentExample.class);
  }

  /**
   * Tests {@code bindingsOf} on a {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestBindingsOf_MessageFragment() {
    assertThat(example.fragment,
        hasBindings(example.arg2.getDestination(), example.arg4.getDestination()));
  }

  private Matcher<InteractionFragment> hasBindings(Variable... xs) {
    return Matchers.resolvesTo(resolver::bindingsOf, xs);
  }
}
