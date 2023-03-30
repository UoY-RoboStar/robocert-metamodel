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

import circus.robocalc.robochart.RoboChartFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.Message;
import robostar.robocert.MessageEnd;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.resolve.EndIndex;

class EndIndexTest {

  private final RoboChartFactory chartFac = RoboChartFactory.eINSTANCE;
  private final ActorFactory actFac = ActorFactory.DEFAULT;
  private final MessageFactory msgFac = MessageFactory.DEFAULT;

  private Message msg;
  private MessageEnd to;
  private MessageEnd from;

  @BeforeEach
  void setUp() {
    final var e = chartFac.createEvent();
    e.setName("evt");

    from = msgFac.occurrence(actFac.targetActor("T"));
    to = msgFac.gate();
    msg = msgFac.message(from, to, msgFac.eventTopic(e));
  }

  /**
   * Tests {@code of}.
   */
  @Test
  void testOf() {
    assertThat(EndIndex.From.of(msg), is(from));
    assertThat(EndIndex.To.of(msg), is(to));
  }

  /**
   * Tests {@code opposite} exhaustively.
   */
  @Test
  void testOpposite() {
    assertThat(EndIndex.From.opposite(), is(EndIndex.To));
    assertThat(EndIndex.To.opposite(), is(EndIndex.From));
  }

  /**
   * Tests {@code oppositeIf} exhaustively.
   */
  @Test
  void testOppositeIf() {
    assertThat(EndIndex.From.oppositeIf(true), is(EndIndex.To));
    assertThat(EndIndex.To.oppositeIf(true), is(EndIndex.From));
    assertThat(EndIndex.From.oppositeIf(false), is(EndIndex.From));
    assertThat(EndIndex.To.oppositeIf(false), is(EndIndex.To));
  }
}
