/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.*;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.util.StreamHelper;

/**
 * Resolves various aspects of modules.
 *
 * @param defRes utility for resolving references into definitions.
 * @author Matt Windsor
 */
public record ModuleResolver(DefinitionResolver defRes) implements NameResolver<RCModule> {

  /**
   * Constructs a module resolver.
   *
   * @param defRes utility for resolving references into definitions.
   */
  @Inject
  public ModuleResolver {
    Objects.requireNonNull(defRes);
  }

  @Override
  public String[] name(RCModule mod) {
    // TODO(@MattWindsor91): this duplicates GeneratorUtils code.
    final var pkg = ResolveHelper.packageOf(mod).map(RCPackage::getName);
    final var name = unqualifiedName(mod);

    return Stream.concat(pkg.stream(), Stream.of(name)).toArray(String[]::new);
  }

  @Override
  public String unqualifiedName(RCModule mod) {
    return mod.getName();
  }

  /**
   * Gets the robotic platform definition for a RoboChart module.
   *
   * @param m the RoboChart module.
   * @return the module's robotic platform, if it has one.
   */
  public Optional<RoboticPlatformDef> platform(RCModule m) {
    return nodes(m, RoboticPlatform.class).map(defRes::resolve).findFirst();
  }

  /**
   * Gets the controller definitions for a RoboChart module.
   *
   * @param it the RoboChart module
   * @return the module's controllers
   */
  public Stream<Controller> controllers(RCModule it) {
    return nodes(it, Controller.class);
  }

  /**
   * Gets all connections in m that are not connected to the robotic platform.
   *
   * @param mod the module in question
   * @return a stream of connections connecting something in the module to something else not in the
   * robotic platform
   */
  public Stream<Connection> inboundConnections(RCModule mod) {
    return mod.getConnections().stream().filter(x -> !connectsPlatform(x));
  }

  /**
   * Gets all connections in m that are connected to the robotic platform.
   *
   * @param mod the module in question
   * @return a stream of connections connecting the robotic platform to something in the module
   */
  public Stream<Connection> outboundConnections(RCModule mod) {
    return mod.getConnections().stream().filter(this::connectsPlatform);
  }


  private boolean connectsPlatform(Connection c) {
    return c.getFrom() instanceof RoboticPlatform || c.getTo() instanceof RoboticPlatform;
  }

  private <T extends ConnectionNode> Stream<T> nodes(RCModule mod, Class<T> tClass) {
    final var nodes = Stream.ofNullable(mod).flatMap(m -> m.getNodes().parallelStream());
    return StreamHelper.filter(nodes, tClass);
  }
}
