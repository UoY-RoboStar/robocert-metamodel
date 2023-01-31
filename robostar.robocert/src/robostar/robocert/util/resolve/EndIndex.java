/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import robostar.robocert.Message;
import robostar.robocert.MessageEnd;

/**
 * Indices that can be used for programmatically retrieving a {@link MessageEnd} from a
 * {@link Message}.
 *
 * @author Matt Windsor
 */
public enum EndIndex {
  /**
   * The from-end.
   */
  From,
  /**
   * The to-end.
   */
  To;

  /**
   * Indexes a message by this index.
   *
   * @param m the message to index
   * @return the from-index if this is {@code From}; the to-index if it is {@code To}
   */
  public MessageEnd of(Message m) {
    return switch (this) {
      case From -> m.getFrom();
      case To -> m.getTo();
    };
  }

  /**
   * Inverts this index.
   *
   * @return the opposite of this index ('from' if it is 'to', and 'to' if it is 'from')
   */
  public EndIndex opposite() {
    return switch (this) {
      case From -> To;
      case To -> From;
    };
  }

  /**
   * Conditionally inverts this index.
   *
   * @param cond the condition for taking the opposite of this index
   * @return the opposite of this index if the condition is satisfied, and this index otherwise
   */
  public EndIndex oppositeIf(boolean cond) {
    return cond ? opposite() : this;
  }
}
