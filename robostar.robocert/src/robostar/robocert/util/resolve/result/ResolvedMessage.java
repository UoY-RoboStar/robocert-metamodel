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

import java.util.Objects;
import java.util.Set;
import robostar.robocert.Actor;
import robostar.robocert.Message;

/**
 * Contains a message and various pieces of pre-calculated information about it.
 *
 * @param message the message in question
 * @param topic   the resolved topic of the message
 * @param actors  the pre-calculated set of actors connected by the message
 * @author Matt Windsor
 */
public record ResolvedMessage(Message message, ResolvedTopic topic, Set<Actor> actors) {

  public ResolvedMessage {
    Objects.requireNonNull(message, "message must be non-null");
    Objects.requireNonNull(topic, "topic must be non-null");
    Objects.requireNonNull(actors, "actors must be non-null");
  }
}
