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

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RoboChartFactory;
import circus.robocalc.robochart.StateMachineDef;
import robostar.robocert.ComponentActor;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.util.factory.MessageFactory;
import robostar.robocert.util.factory.TargetFactory;

/**
 * Tests custom functionality on Messages.
 *
 * @author Matt Windsor
 */
class MessageImplTest {

  private final MessageFactory msgFac = new MessageFactory(RoboCertFactory.eINSTANCE);
  private final RoboChartFactory chartFactory = RoboChartFactory.eINSTANCE;
  private final RoboCertFactory certFactory = RoboCertFactory.eINSTANCE;
  private final TargetFactory targetFactory = new TargetFactory(RoboCertFactory.eINSTANCE);

  private TargetActor comTarget;

  private ComponentActor c1;
  private ComponentActor c2;

  @BeforeEach
  void setUp() {
    final var stm1 = chartFactory.createStateMachineDef();
    stm1.setName("Stm1");
    final var stm2 = chartFactory.createStateMachineDef();
    stm2.setName("Stm1");

    final var ctrl = chartFactory.createControllerDef();
    ctrl.setName("Ctrl");
    ctrl.getMachines().addAll(List.of(stm1, stm2));

    setUpComponent(ctrl);
    setUpCollection(stm1, stm2, ctrl);
  }

  private void setUpComponent(ControllerDef ctrl) {
    final var comTgt = targetFactory.controller(ctrl);
    final var comGrp = certFactory.createSpecificationGroup();
    comGrp.setName("ComGroup");
    comGrp.setTarget(comTgt);

    comTarget = msgFac.targetActor("T");
    comGrp.getActors().add(comTarget);
  }

  private void setUpCollection(StateMachineDef stm1, StateMachineDef stm2, ControllerDef ctrl) {
    final var collTgt = targetFactory.inController(ctrl);
    final var collGrp = certFactory.createSpecificationGroup();
    collGrp.setName("CollGroup");
    collGrp.setTarget(collTgt);

    c1 = certFactory.createComponentActor();
    c1.setName("C1");
    c1.setNode(stm1);
    c2 = certFactory.createComponentActor();
    c2.setName("C2");
    c2.setNode(stm2);
    collGrp.getActors().addAll(List.of(c1, c2));
  }

  @Test
  void testIsOutbound_component() {
    final var e = chartFactory.createEvent();
    e.setName("e");
    final var topic = msgFac.eventTopic(e);

    // All messages in a component context are outbound.
    final var msg1 = msgFac.spec(msgFac.actor(comTarget), msgFac.world(), topic);
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFac.spec(msgFac.world(), msgFac.actor(comTarget), topic);
    assertThat(msg2.isOutbound(), is(true));
  }

  @Test
  void testIsOutbound_collection() {
    final var e = chartFactory.createEvent();
    e.setName("e");
    final var topic = msgFac.eventTopic(e);

    // All messages with a world (and only those) are outbound:

    final var msg1 = msgFac.spec(msgFac.actor(c1), msgFac.world(), topic);
    assertThat(msg1.isOutbound(), is(true));

    final var msg2 = msgFac.spec(msgFac.world(), msgFac.actor(c2), topic);
    assertThat(msg2.isOutbound(), is(true));

    final var msg3 = msgFac.spec(msgFac.actor(c1), msgFac.actor(c2), topic);
    assertThat(msg3.isOutbound(), is(false));
  }
}
