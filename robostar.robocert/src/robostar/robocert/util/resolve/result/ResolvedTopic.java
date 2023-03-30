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

import robostar.robocert.util.resolve.EndIndex;

/**
 * A resolved topic.
 *
 * @author Matt Windsor
 */
public sealed interface ResolvedTopic permits ResolvedOperation, ResolvedEvent {
  /**
   * Gets the 'effective' from-end of this message.
   *
   * <p>
   * This end is important because, in certain definitions of the RoboChart semantics (for instance,
   * rule 15 of the CSP semantics), the to-end of synchronous messages is subsumed into the from-end
   * for synchronisation.
   *
   * <p>
   * We define the effective from-end for messages involving a gate as whichever end is not that
   * gate.  Otherwise, we define it as the actual from-end if we matched forwards, and the actual
   * to-end if we matched backwards.
   *
   * @return the index of the effective from-end
   */
  EndIndex effectiveFrom();
}
