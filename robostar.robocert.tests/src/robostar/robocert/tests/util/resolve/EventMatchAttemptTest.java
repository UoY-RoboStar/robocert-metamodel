/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.tests.util.resolve;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.google.inject.Guice;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.tests.examples.MessageResolveExample;
import robostar.robocert.util.RoboCertBaseModule;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.resolve.message.EventMatchAttempt;
import robostar.robocert.util.resolve.result.MatchDirection;
import robostar.robocert.util.resolve.result.MessageEndNodesPair;

/**
 * Tests {@link EventMatchAttempt}.
 *
 * @author Matt Windsor
 */
class EventMatchAttemptTest {

  private MessageFactory msgFac;


  private MessageResolveExample example;

  @BeforeEach
  void setUp() {
    final var inj = Guice.createInjector(new RoboCertBaseModule());

    msgFac = inj.getInstance(MessageFactory.class);
    example = inj.getInstance(MessageResolveExample.class);
  }

  /**
   * Tests resolution on a basic non-bidirectional event.
   */
  @Test
  void testResolve_Event() {
    final var nodePair = new MessageEndNodesPair(Set.of(example.rp), Set.of(example.ctrl));
    final var topic = msgFac.eventTopic(example.event);
    final var match = new EventMatchAttempt(topic, example.conn, nodePair);
    assertThat(match.tryMatch(), is(Optional.of(MatchDirection.FORWARDS)));
  }
}
