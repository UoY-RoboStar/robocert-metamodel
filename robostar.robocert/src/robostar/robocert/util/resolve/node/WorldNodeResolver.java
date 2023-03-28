/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.node;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineBody;
import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.resolve.ControllerResolver;
import robostar.robocert.util.resolve.ModuleResolver;
import robostar.robocert.util.resolve.StateMachineResolver;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Resolves worlds into the connection nodes that can represent them.
 * <p>
 * This resolver does not take into account the fact that some world nodes shadow actors already
 * inside a diagram.  Upstream consumers of this resolver must handle such a situation themselves,
 * by filtering out those nodes.
 *
 * @param modRes       helper for resolving aspects of RoboChart modules.
 * @param ctrlRes      helper for resolving aspects of RoboChart controllers.
 * @param stmRes       helper for resolving aspects of RoboChart state machines and operations.
 * @param aNodeRes     helper for resolving actors into connection nodes.
 */
public record WorldNodeResolver(ModuleResolver modRes, ControllerResolver ctrlRes,
                                StateMachineResolver stmRes, ActorNodeResolver aNodeRes) {
  // TODO(@MattWindsor91): DRY up with the other NodeResolvers.

  /**
   * Constructs a world resolver.
   *
   * @param ctrlRes      helper for resolving aspects of RoboChart controllers.
   * @param modRes       helper for resolving aspects of RoboChart modules.
   * @param stmRes       helper for resolving aspects of RoboChart state machines and operations.
   * @param aNodeRes     helper for resolving actors into connection nodes.
   */
  @Inject
  public WorldNodeResolver {
    Objects.requireNonNull(modRes);
    Objects.requireNonNull(ctrlRes);
    Objects.requireNonNull(stmRes);
    Objects.requireNonNull(aNodeRes);
  }

  /**
   * Deduces a stream of connection nodes that can represent the world for a target.
   *
   * <p>The stream may contain more than one node.
   *
   * @param target the target for which we are resolving target-relative actors.
   * @return a stream of connection nodes that can represent the target actor.
   */
  public Stream<ConnectionNode> resolve(Target target) {
    return new RoboCertSwitch<Stream<ConnectionNode>>() {
      @Override
      public Stream<ConnectionNode> defaultCase(EObject e) {
        throw new IllegalArgumentException("can't resolve world actor for target %s".formatted(e));
      }

      /**
       * Gets the world of a {@link ModuleTarget}.
       *
       * <p>
       * This is the robotic platform.
       *
       * @param t the target of the switch
       * @return the world of the target as a stream
       */
      @Override
      public Stream<ConnectionNode> caseModuleTarget(ModuleTarget t) {
        return roboticPlatform(t.getModule());
      }

      /**
       * Gets the world of an {@link InModuleTarget}.
       *
       * <p>
       * This is the robotic platform, plus all controllers.
       *
       * @param t the target of the switch
       * @return the world of the target as a stream
       */
      @Override
      public Stream<ConnectionNode> caseInModuleTarget(InModuleTarget t) {
        final var mod = t.getModule();
        return Stream.concat(roboticPlatform(mod), modRes.controllers(mod));
      }

      /**
       * Gets the world of a {@link ControllerTarget}.
       *
       * <p>
       * This is the robotic platform plus the set of all controllers in the parent module that are
       * not the target.
       *
       * @param t the target of the switch
       * @return the world of the target as a stream
       */
      @Override
      public Stream<ConnectionNode> caseControllerTarget(ControllerTarget t) {
        final var ctrl = t.getController();
        return ctrlRes.module(ctrl).stream().flatMap(WorldNodeResolver.this::moduleChildren)
            .filter(x -> x != ctrl);
      }

      /**
       * Gets the world of an {@link InControllerTarget}.
       *
       * <p>
       * This is the controller plus all of its children.
       *
       * @param t the target of the switch
       * @return the world of the target as a stream
       */
      @Override
      public Stream<ConnectionNode> caseInControllerTarget(InControllerTarget t) {
        final var ctrl = t.getController();
        return Stream.concat(Stream.of(ctrl), controllerChildren(ctrl));
      }

      /**
       * Gets the world of an {@link StateMachineTarget}.
       *
       * <p>
       * This is the parent controller and any state machines or operations adjacent to this one.
       *
       * @param t the target of the switch
       * @return the world of the target as a stream
       */
      @Override
      public Stream<ConnectionNode> caseStateMachineTarget(StateMachineTarget t) {
        return stmBodyWorld(t.getStateMachine());
      }

      @Override
      public Stream<ConnectionNode> caseOperationTarget(OperationTarget t) {
        return stmBodyWorld(t.getOperation());
      }
    }.doSwitch(target);
  }

  private Stream<ConnectionNode> roboticPlatform(RCModule m) {
    // The x -> x is there to cast to ConnectionNode.
    return modRes.platform(m).stream().map(x -> x);
  }

  private Stream<ConnectionNode> stmBodyWorld(StateMachineBody s) {
    // The world of a state machine or operation is the controller and everything within it,
    // except the state machine body itself.
    return stmRes.controller(s).stream().flatMap(this::controllerAndChildren).filter(x -> x != s);
  }

  private Stream<ConnectionNode> moduleChildren(RCModule m) {
    return m.getNodes().stream();
  }

  private Stream<ConnectionNode> controllerAndChildren(ControllerDef c) {
    return Stream.concat(Stream.of(c), controllerChildren(c));
  }


  private Stream<ConnectionNode> controllerChildren(ControllerDef c) {
    return Stream.concat(c.getLOperations().stream(), c.getMachines().stream());
  }
}
