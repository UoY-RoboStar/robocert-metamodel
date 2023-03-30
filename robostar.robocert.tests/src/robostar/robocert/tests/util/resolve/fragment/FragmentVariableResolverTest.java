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
import static org.hamcrest.Matchers.containsInAnyOrder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.tests.examples.FragmentExample;
import robostar.robocert.util.resolve.fragment.FragmentVariableResolver;

/**
 * Tests fragment variable resolution.
 *
 * @author Matt Windsor
 */
class FragmentVariableResolverTest {

  private FragmentVariableResolver resolver;
  private FragmentExample example;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();
    resolver = inj.getInstance(FragmentVariableResolver.class);
    example = inj.getInstance(FragmentExample.class);
  }

  /**
   * Tests {@code bindingsOf} on an example {@link robostar.robocert.MessageFragment}.
   */
  @Test
  void TestBindingsOf_MessageFragment() {
    final var vars = resolver.variablesOf(example.fragment);
    assertThat(vars.inputs(), containsInAnyOrder(example.var3));
    assertThat(vars.outputs(),
        containsInAnyOrder(example.arg2.getDestination(), example.arg4.getDestination()));
  }
}
