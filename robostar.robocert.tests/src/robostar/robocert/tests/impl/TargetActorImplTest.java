/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robostar.robocert.tests.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import robostar.robocert.RoboCertFactory;

/**
 * Tests the string representation for target actors, and also tests that
 * the factory resolves them correctly.
 *
 * @author Matt Windsor
 */
class TargetActorImplTest {

	private RoboCertFactory rf = RoboCertFactory.eINSTANCE;

	@Test
	void testToString() {
		final var actor = rf.createTargetActor();
		assertThat(actor.toString(), is(equalTo("<<target>> (untitled)")));
		actor.setName("test");
		assertThat(actor.toString(), is(equalTo("<<target>> test")));
	}
}
