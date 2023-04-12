/* Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import circus.robocalc.robochart.Event;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import robostar.robocert.ComponentActor;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.tests.TestInjectorProvider;
import robostar.robocert.util.factory.ActorFactory;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.robochart.EventFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;

/**
 * Tests custom functionality on Messages.
 *
 * @author Matt Windsor
 */
class MessageImplTest {

  private MessageFactory msgFac;

  private TargetActor comTarget;

  private ComponentActor c1;
  private ComponentActor c2;

  private Event event;

  @BeforeEach
  void setUp() {
    final var inj = TestInjectorProvider.getInjector();
    msgFac = inj.getInstance(MessageFactory.class);

    final var chartFac = inj.getInstance(RoboChartBuilderFactory.class);
    final var stm1 = chartFac.stmDef("Stm1").get();
    final var stm2 = chartFac.stmDef("Stm2").get();
    final var ctrl = chartFac.controllerDef("Ctrl").machines(stm1, stm2).get();

    final var actFac = inj.getInstance(ActorFactory.class);
    comTarget = actFac.targetActor("T");
    c1 = actFac.componentActor("C1", stm1);
    c2 = actFac.componentActor("C2", stm2);

    final var certFac = inj.getInstance(RoboCertFactory.class);
    final var tgtFac = inj.getInstance(TargetFactory.class);

    final var comGrp = certFac.createSpecificationGroup();
    comGrp.setName("ComGroup");
    comGrp.setTarget(tgtFac.controller(ctrl));
    comGrp.getActors().add(comTarget);

    final var collGrp = certFac.createSpecificationGroup();
    collGrp.setName("CollGroup");
    collGrp.setTarget(tgtFac.inController(ctrl));
    collGrp.getActors().addAll(List.of(c1, c2));

    event = inj.getInstance(EventFactory.class).event("e");
  }

  @Test
  void testIsOutbound_component() {
    // All messages in a component context are outbound.
    final var msg1 = msgFac.sync(event).from(comTarget).toGate().get();
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFac.sync(event).fromGate().to(comTarget).get();
    assertThat(msg2.isOutbound(), is(true));
  }

  @Test
  void testIsOutbound_collection() {
    // All messages with a world (and only those) are outbound:

    final var msg1 = msgFac.sync(event).from(c1).toGate().get();
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFac.sync(event).fromGate().to(c2).get();
    assertThat(msg2.isOutbound(), is(true));

    final var msg3 = msgFac.sync(event).from(c1).to(c2).get();
    assertThat(msg3.isOutbound(), is(false));
  }
}
