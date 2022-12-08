/* Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.tests.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import robostar.robocert.RoboCertFactory;
import robostar.robocert.World;

/**
 * Tests the string representation for {@link World}s, and also tests that
 * the factory resolves them correctly.
 *
 * @author Matt Windsor
 */
class WorldImplTest {

	private final RoboCertFactory rf = RoboCertFactory.eINSTANCE;

	/**
	 * Tests that stringifying a world works as expected.
	 */
	@Test
	void testToString() {
		final var world = rf.createWorld();
		assertThat(world.toString(), is(equalTo("<<world>>")));
	}
}
