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

import java.util.NoSuchElementException;
import java.util.Objects;
import javax.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.EventTopic;
import robostar.robocert.Message;
import robostar.robocert.OperationTopic;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.resolve.node.ResolveContext;
import robostar.robocert.util.resolve.result.ResolvedOperation;
import robostar.robocert.util.resolve.result.ResolvedTopic;

public record TopicResolver(EventResolver eventRes) {

  @Inject
  public TopicResolver {
    Objects.requireNonNull(eventRes);
  }

  /**
   * Resolves a topic in the context of its parent message.
   *
   * @param msg the message containing the topic
   * @param ctx the wider resolve context (target and live actors)
   * @return the resolved topic
   */
  public ResolvedTopic resolve(Message msg, ResolveContext ctx) {
    Objects.requireNonNull(msg, "message cannot be null");
    Objects.requireNonNull(ctx, "context cannot be null");
    final var topic = Objects.requireNonNull(msg.getTopic(), "topic cannot be null");

    return new RoboCertSwitch<ResolvedTopic>() {
      @Override
      public ResolvedTopic defaultCase(EObject object) {
        throw new IllegalArgumentException("unsupported topic: %s".formatted(object));
      }

      @Override
      public ResolvedTopic caseEventTopic(EventTopic e) {
        // Events are complicated enough to delegate to a separate generator.
        final var query = new EventResolverQuery(msg, e, ctx);
        final var events = eventRes.resolve(query);
        return events.findFirst().orElseThrow(
            () -> new NoSuchElementException("no event found for query %s".formatted(query)));
      }

      @Override
      public ResolvedTopic caseOperationTopic(OperationTopic o) {
        return new ResolvedOperation(o);
      }
    }.doSwitch(topic);
  }
}
