/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.result;

/**
 * Enumeration of directions in which an event can be resolved.
 *
 * @author Matt Windsor
 */
public enum MatchDirection {
  /**
   * The query matched this event directly, without swapping direction.
   */
  FORWARDS,
  /**
   * The query matched this (bidirectional) event by swapping direction.
   */
  BACKWARDS
}
