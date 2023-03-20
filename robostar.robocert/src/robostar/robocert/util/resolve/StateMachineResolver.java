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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import com.google.inject.Inject;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineBody;
import circus.robocalc.robochart.StateMachineDef;

/**
 * Resolves various aspects of state machines (including operations).
 *
 * @param ctrlRes a controller resolver, used to get controller names
 * @author Matt Windsor
 */
public record StateMachineResolver(ControllerResolver ctrlRes) implements
    NameResolver<StateMachineBody> {

  /**
   * Constructs a state machine resolver.
   *
   * @param ctrlRes a controller resolver, used to get controller names
   */
  @Inject
  public StateMachineResolver {
    Objects.requireNonNull(ctrlRes);
  }

  /**
   * Gets the enclosing controller for a RoboChart state machine or operation.
   * <p>
   * This assumes that the item is inside a controller.
   *
   * @param b the RoboChart state machine body (state machine or operation)
   * @return the body's controller, if it has one.
   */
  public Optional<ControllerDef> controller(StateMachineBody b) {
    return ResolveHelper.containerOfType(b, ControllerDef.class);
  }

  /**
   * Gets the enclosing module for a RoboChart state machine or operation.
   * <p>
   * This assumes that the item is inside a module.
   *
   * @param b the RoboChart state machine body (state machine or operation)
   * @return the body's module, if it has one
   */
  public Optional<RCModule> module(StateMachineBody b) {
    return ResolveHelper.containerOfType(b, RCModule.class);
  }

  @Override
  public String[] name(StateMachineBody element) {
    final var ctrl = controller(element);
    return ctrl.map(c -> nameInController(element, c)).orElseGet(() -> {
      // State machine is not inside a controller.
      return module(element).map(m -> nameInModule(element, m)).orElse(null);
    });
  }

  private String[] nameInController(StateMachineBody element, ControllerDef ctrl) {
    final var cname = ctrlRes.name(ctrl);
    final var name = Arrays.copyOf(cname, cname.length + 1);
    name[cname.length] = unqualifiedName(element);
    return name;
  }

  private String[] nameInModule(StateMachineBody element, RCModule mod) {
    return new String[]{mod.getName(), unqualifiedName(element)};
  }

  /**
   * Gets the unqualified name of this state machine body.
   *
   * @param b the state machine body for which we are getting the body
   * @return the unqualified name
   * @throws IllegalArgumentException if the type of state machine body is unknown
   * @apiNote this does not add 'OP_' to operations
   */
  @Override
  public String unqualifiedName(StateMachineBody b) {
    if (b instanceof StateMachineDef d) {
      return d.getName();
    }
    if (b instanceof OperationDef d) {
      return d.getName();
    }
    throw new IllegalArgumentException("can't get name of state machine body %s".formatted(b));
  }
}
