/*******************************************************************************
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
 ******************************************************************************/
package robostar.robocert.tests.matchers;

import org.eclipse.emf.ecore.EObject;

/**
 * Hamcrest matchers for RoboCert tests.
 *
 * @author Matt Windsor
 */
public class Matchers {

	/**
	 * Constructs a is-structurally-equal matcher with the given expected object.
	 *
	 * @param expected the object to test against.
	 */	
	public static <T extends EObject> IsStructurallyEqualTo<T> structurallyEqualTo(T expected) {
		return new IsStructurallyEqualTo<>(expected);
	}
}
