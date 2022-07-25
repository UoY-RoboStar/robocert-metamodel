/*******************************************************************************
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
 ******************************************************************************/
package robostar.robocert.impl;

/**
 * Prefixes used for constructing string representations of targets.
 * 
 * This sidesteps a problem with Emfatic where we can't nest string literals
 * into injected bodies; instead of giving the toString() definitions inline,
 * we just call out into this class.
 * 
 * @author Matt Windsor
 *
 */
public class TargetStringer {
	/**
	 * Prefix of controller targets.
	 */
	public static final String MODULE = "module";

	/**
	 * Prefix of controller targets.
	 */
	public static final String CONTROLLER = "controller";

	/**
	 * Prefix of state machine targets.
	 */
	public static final String STATE_MACHINE = "state machine";

	/**
	 * Prefix of operation targets.
	 */
	public static final String OPERATION = "operation";
	
	/**
	 * Produces the name of a collection target.
	 * @param type prefix for the type of component.
	 * @param name name of the component.
	 * @return human-friendly string representation of the target.
	 */
	public static String collection(String type, String name) {
		return new StringBuilder().append("components of").append(type).append(' ').append(name).toString();
	}

	/**
	 * Produces the name of a component target.
	 * @param type prefix for the type of component.
	 * @param name name of the component.
	 * @return human-friendly string representation of the target.
	 */
	public static String component(String type, String name) {
		return new StringBuilder().append(type).append(' ').append(name).toString();
	}
}
