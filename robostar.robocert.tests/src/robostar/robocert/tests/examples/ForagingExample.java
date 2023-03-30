/*
 * Copyright (c) 2022-2023 University of York and others
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
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.StateMachineDef;
import javax.inject.Inject;
import robostar.robocert.util.factory.robochart.ConnectionFactory;
import robostar.robocert.util.factory.robochart.RoboChartBuilderFactory;
import robostar.robocert.util.factory.robochart.EventFactory;

/**
 * Programmatic encoding of a simplified version of the Buchanan foraging robot case study.
 * <p>
 * This encodes a case study due to Edgar Buchanan, Andrew Pomfret, and Jon Timmis, and initially
 * converted to RoboChart by Alvaro Miyazawa, Pedro Ribeiro, Wei Li, and Ana Cavalcanti.
 *
 * @author Matt Windsor
 */
public class ForagingExample {

  public Event platformObstacle;
  public Event obstacleAvoidanceObstacle;
  public Event avoidObstacle;

  public RCModule foraging;
  public RoboticPlatformDef platform;
  public ControllerDef obstacleAvoidance;
  public StateMachineDef avoid;

  public Connection obstaclePlatformToObstacleAvoidance;
  public Connection obstacleObstacleAvoidanceToAvoid;

  /**
   * Constructs the foraging example.
   *
   * @param connFac  a connection factory
   * @param evFac    an event factory
   * @param chartFac a RoboChart builder factory
   */
  @Inject
  public ForagingExample(ConnectionFactory connFac, EventFactory evFac,
      RoboChartBuilderFactory chartFac) {
    platformObstacle = evFac.event("obstacle");
    avoidObstacle = evFac.event("obstacle");
    obstacleAvoidanceObstacle = evFac.event("obstacle");

    avoid = chartFac.stmDef("Avoid").events(avoidObstacle).get();

    obstacleAvoidance = chartFac.controllerDef("ObstacleAvoidance")
        .events(obstacleAvoidanceObstacle).machines(avoid).get();

    obstacleObstacleAvoidanceToAvoid = connFac.from(obstacleAvoidance, obstacleAvoidanceObstacle)
        .to(avoid, avoidObstacle);
    obstacleAvoidance.getConnections().add(obstacleObstacleAvoidanceToAvoid);

    platform = chartFac.rpDef("Platform").events(platformObstacle).get();

    obstaclePlatformToObstacleAvoidance = connFac.from(platform, platformObstacle)
        .to(obstacleAvoidance, obstacleAvoidanceObstacle);

    foraging = chartFac.module("Foraging", platform).nodes(obstacleAvoidance)
        .connections(obstaclePlatformToObstacleAvoidance).get();
  }

}
