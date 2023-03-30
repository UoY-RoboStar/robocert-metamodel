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

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.Event;
import robostar.robocert.EventTopic;
import robostar.robocert.Message;
import robostar.robocert.MessageEnd;

/**
 * Indices that can be used for programmatically retrieving a {@link MessageEnd} from a
 * {@link Message}, or the corresponding {@link Event} from a {@link Connection} or
 * {@link EventTopic}.
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
   * @param message the message to index
   * @return the from-end if this is {@code From}; the to-end if it is {@code To}
   */
  public MessageEnd of(Message message) {
    return switch (this) {
      case From -> message.getFrom();
      case To -> message.getTo();
    };
  }

  /**
   * Indexes a topic by this index, retrieving an event.
   *
   * @param event the topic to index
   * @return the from-event if this is {@code From}; the to-event if it is {@code To}
   */
  public Event eventOf(EventTopic event) {
    return switch (this) {
      case From -> event.getEfrom();
      case To -> event.getEto();
    };
  }

  /**
   * Indexes a connection by this index, retrieving an event.
   *
   * @param conn the connection to index
   * @return the from-event if this is {@code From}; the to-event if it is {@code To}
   */
  public Event eventOf(Connection conn) {
    return switch (this) {
      case From -> conn.getEfrom();
      case To -> conn.getEto();
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
