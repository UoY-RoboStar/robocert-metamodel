/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.util.resolve.message;

import com.google.common.collect.Streams;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Inject;
import robostar.robocert.Actor;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.MessageEnd;
import robostar.robocert.Interaction;
import robostar.robocert.Message;
import robostar.robocert.util.StreamHelper;
import robostar.robocert.util.resolve.node.ResolveContext;
import robostar.robocert.util.resolve.result.ResolvedMessage;

/**
 * Resolves various aspects of messages.
 *
 * @author Matt Windsor
 */
public record MessageResolver(TopicResolver topicRes) {
  @Inject
  public MessageResolver {
    Objects.requireNonNull(topicRes);
  }

  /**
   * Gets the messages referenced by fragments in an interaction.
   *
   * @param seq the interaction for which we are getting messages
   * @return all messages contained in the interaction (recursively considering fragments)
   */
  @SuppressWarnings("UnstableApiUsage")
  public Stream<Message> messages(Interaction seq) {
    return StreamHelper.filter(Streams.stream(seq.eAllContents()), Message.class);
  }

  /**
   * Fully resolves a message.
   *
   * @param msg the message
   * @param ctx the context (target and live actor list)
   * @return the message combined with information about its actors and topic
   */
  public ResolvedMessage resolve(Message msg, ResolveContext ctx) {
    final var actors = actors(msg).collect(Collectors.toUnmodifiableSet());
    final var topic = topicRes.resolve(msg, ctx);

    return new ResolvedMessage(msg, topic, actors);
  }

  /**
   * Gets all actors referenced by a message.
   *
   * @param m the message to inspect
   * @return a stream of actors referenced within actor endpoints in the given message
   */
  public Stream<Actor> actors(Message m) {
    return StreamHelper.filter(ends(m), MessageOccurrence.class).map(MessageOccurrence::getActor);
  }

  /**
   * Gets all message ends referenced by a message.
   *
   * @param m the message to inspect
   * @return a stream of (from, to) endpoints
   */
  public Stream<MessageEnd> ends(Message m) {
    return Stream.of(m.getFrom(), m.getTo());
  }

}
