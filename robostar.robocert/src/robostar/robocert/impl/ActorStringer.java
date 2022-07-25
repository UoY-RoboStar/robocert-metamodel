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

import com.google.common.base.Strings;

/**
 * Prefixes used for constructing string representations of actors.
 * 
 * This sidesteps a problem with Emfatic where we can't nest string literals
 * into injected bodies; instead of giving the toString() definitions inline,
 * we just call out into this class.
 * 
 * @author Matt Windsor
 *
 */
public class ActorStringer {
	/**
	 * Type string for target actors.
	 */
	public static String TARGET = "target";
	
	/**
	 * Type string for component actors.
	 */
	public static String COMPONENT = "component";
	
	/**
	 * Type string for worlds.
	 */
	public static String WORLD = "world";
	
	/**
	 * Produces a string representation for a singleton actor.
	 * @param type prefix for the type of actor.
	 * @param actorName name of the actor (can be null).
	 * @return human-friendly string representation of the actor.
	 */
	public static String singleton(String type, String actorName) {
		return prefix(type, actorName).toString();
	}

	/**
	 * Produces a string representation for a non-singleton actor.
	 * @param type prefix for the type of actor.
	 * @param actorName name of the actor (can be null).
	 * @param compName name of the component.
	 * @return human-friendly string representation of the actor.
	 */
	public static String multi(String type, String actorName, String compName) {
		return prefix(type, actorName).append(": ").append(compName).toString();
	}

	private static StringBuffer prefix(String type, String actorName) {
		if (Strings.isNullOrEmpty(actorName)) {
			actorName = "(untitled)";
		}
		return new StringBuffer().append("<<").append(type).append(">> ").append(actorName);
	}
}
