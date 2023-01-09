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

import com.google.common.collect.Streams;
import java.util.stream.Stream;
import robostar.robocert.Actor;
import robostar.robocert.ActorEndpoint;
import robostar.robocert.Endpoint;
import robostar.robocert.Interaction;
import robostar.robocert.Message;
import robostar.robocert.util.StreamHelper;

/**
 * Resolves various aspects of messages.
 *
 * @author Matt Windsor
 */
public class MessageResolver {

  /**
   * Gets the messages referenced by fragments in an interaction.
   *
   * @param seq interaction for which we are getting messages.
   * @return all messages contained in the interaction (recursively considering fragments).
   */
  @SuppressWarnings("UnstableApiUsage")
  public Stream<Message> messages(Interaction seq) {
    return StreamHelper.filter(Streams.stream(seq.eAllContents()), Message.class);
  }

  /**
   * Gets all actors referenced by a message.
   *
   * @param m message to inspect.
   * @return stream of actors referenced within actor endpoints in the given message.
   */
  public Stream<Actor> actors(Message m) {
    return StreamHelper.filter(endpoints(m), ActorEndpoint.class).map(ActorEndpoint::getActor);
  }

  /**
   * Gets all endpoints referenced by a message.
   *
   * @param m message to inspect.
   * @return stream of (from, to) endpoints.
   */
  public Stream<Endpoint> endpoints(Message m) {
    return Stream.of(m.getFrom(), m.getTo());
  }

}
