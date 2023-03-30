/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.tests.examples;

import circus.robocalc.robochart.Connection;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import circus.robocalc.robochart.RoboticPlatformDef;
import javax.inject.Inject;
import robostar.robocert.ModuleTarget;
import robostar.robocert.util.factory.TargetFactory;
import robostar.robocert.util.factory.robochart.ConnectionFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.factory.robochart.EventFactory;
import robostar.robocert.util.factory.robochart.TypeFactory;

/**
 * A toy example used to test message resolution and generation.
 *
 * @author Matt Windsor
 */
public class MessageResolveExample {

  /**
   * A sample module target.
   */
  public final ModuleTarget target;


  /**
   * A sample robotic platform.
   */
  public final RoboticPlatformDef rp;

  /**
   * A sample controller.
   */
  public final ControllerDef ctrl;

  /**
   * A sample operation, from a platform to the controller.
   */
  public final Event event;

  /**
   * The connection to which event should be resolved.
   */
  public final Connection conn;

  /**
   * A sample operation, from a controller to the platform.
   */
  public final OperationSig op;

  @Inject
  public MessageResolveExample(ConnectionFactory connFac, RoboChartBuilderFactory chartFac,
      EventFactory evFac, TargetFactory tgtFac, TypeFactory tyFac) {
    final var intType = tyFac.prim("core_int");

    event = evFac.event("event", tyFac.ref(intType));
    final var cevent = evFac.event("cEvent", tyFac.ref(intType));

    op = chartFac.operationSig("op").get();

    rp = chartFac.rpDef("rp").events(event).operations(op).get();

    ctrl = chartFac.controllerDef("ctrl").events(cevent).get();

    conn = connFac.from(rp, event).to(ctrl, cevent);

    // CTRL -- op -> RP
    // TODO(@MattWindsor91): Do we need to log this on the controller?

    final var mod = chartFac.module("test", rp).nodes(ctrl).connections(conn).get();
    target = tgtFac.module(mod);
  }
}
