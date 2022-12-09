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
import robostar.robocert.*;
import robostar.robocert.util.MessageFactory;

/**
 * Test shim used to make sure that endpoints are nested in a group with a
 * valid target.
 *
 * @author Matt Windsor
 */
record EndpointWrapper(RoboCertFactory certFactory, MessageFactory msgFactory) {
    /**
     * Ensures that the given world and target actor are located within a group
     * with the given target.
     *
     * <p>The way this happens is not very sophisticated, but should suffice.
     *
     * @param t      target on which we will create a specification group.
     * @param world  world endpoint.
     * @param target actor endpoint wrapping a target actor.
     */
    public void wrap(Target t, World world, ActorEndpoint target) {
        final var grp = certFactory.createSpecificationGroup();
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
    public void wrap(SpecificationGroup g, World world, ActorEndpoint target) {
        g.getActors().add(target.getActor());

        final var o = RoboChartFactory.eINSTANCE.createOperationSig();
        final var msg = msgFactory.spec(world, target, msgFactory.opTopic(o));
        final var set = certFactory.createExtensionalMessageSet();
        set.getMessages().add(msg);
        final var nset = certFactory.createNamedMessageSet();
        nset.setSet(set);
        g.getMessageSets().add(nset);
    }
}
