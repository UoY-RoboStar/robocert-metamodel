/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.tests.util.resolve;

import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;
import robostar.robocert.*;
import robostar.robocert.util.factory.MessageFactory;

import java.util.Objects;

/**
 * Test shim used to make sure that endpoints are nested in a group with a valid target.
 *
 * @param certFac the underlying RoboCert factory
 * @param msgFac  the underlying message factory
 * @author Matt Windsor
 */
public record MessageEndWrapper(RoboCertFactory certFac, MessageFactory msgFac) {

  /**
   * The default message end wrapper.
   */
  public static final MessageEndWrapper DEFAULT = new MessageEndWrapper(RoboCertFactory.eINSTANCE,
      MessageFactory.DEFAULT);

  /**
   * Constructs an endpoint wrapper
   *
   * @param certFac the underlying RoboCert factory
   * @param msgFac  the underlying message factory
   */
  @Inject
  public MessageEndWrapper {
    Objects.requireNonNull(certFac);
    Objects.requireNonNull(msgFac);
  }


  /**
   * Ensures that the given world and target actor are located within a group with the given
   * target.
   *
   * <p>The way this happens is not very sophisticated, but should suffice.
   *
   * @param t      target on which we will create a specification group.
   * @param world  world endpoint.
   * @param target actor endpoint wrapping a target actor.
   */
  public void wrap(Target t, Gate world, MessageOccurrence target) {
    final var grp = certFac.createSpecificationGroup();
    grp.setName("Grp");
    grp.setTarget(t);
    wrap(grp, world, target);
  }

  /**
   * Ensures that the given world and target are located within the given group.
   *
   * <p>The way this happens is not very sophisticated, but should suffice.
   *
   * @param g      group to use as a target for wrapping.
   * @param world  world endpoint.
   * @param target actor endpoint wrapping a target actor.
   */
  public void wrap(SpecificationGroup g, Gate world, MessageOccurrence target) {
    g.getActors().add(target.getActor());

    final var o = RoboChartFactory.eINSTANCE.createOperationSig();
    final var msg = msgFac.message(world, target, msgFac.opTopic(o));
    final var set = certFac.createExtensionalMessageSet();
    set.getMessages().add(msg);
    final var nset = certFac.createNamedMessageSet();
    nset.setSet(set);
    g.getMessageSets().add(nset);
  }
}
