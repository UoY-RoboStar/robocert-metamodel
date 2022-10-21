/*******************************************************************************
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robostar.robocert.util.resolve;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;

import com.google.inject.Inject;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.StateMachineBody;
import circus.robocalc.robochart.StateMachineDef;

/**
 * Resolves various aspects of state machines (including operations).
 *
 * @param ctrlRes a controller resolver, used to get controller names.
 * @author Matt Windsor
 */
public record StateMachineResolver(ControllerResolver ctrlRes) implements
    NameResolver<StateMachineBody> {

  /**
   * Constructs a state machine resolver.
   *
   * @param ctrlRes a controller resolver, used to get controller names.
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
   * @param b the RoboChart state machine body (state machine or operation).
   * @return the body's controller, if it has one.
   */
  public Optional<ControllerDef> controller(StateMachineBody b) {
    for (EObject e = b; e != null; e = e.eContainer()) {
      if (e instanceof ControllerDef m) {
        return Optional.of(m);
      }
    }

    return Optional.empty();
  }

  @Override
  public String[] name(StateMachineBody element) {
    final var ctrl = controller(element);
    return ctrl.map(c -> nameInController(element, c)).orElseGet(() -> {
      // State machine is not inside a controller.
      for (EObject e = element; e != null; e = e.eContainer()) {
        if (e instanceof RCModule mod) {
          return new String[]{mod.getName(), innerName(element)};
        }
      }
      return null;
    });
  }

  private String[] nameInController(StateMachineBody element, ControllerDef c) {
    final var cname = ctrlRes.name(c);
    final var name = Arrays.copyOf(cname, cname.length + 1);
    name[cname.length] = innerName(element);
    return name;
  }

  private String innerName(StateMachineBody b) {
    if (b instanceof StateMachineDef d) {
      return d.getName();
    }
    if (b instanceof OperationDef d) {
      return "OP_" + d.getName();
    }
    throw new IllegalArgumentException("can't get name of state machine body %s".formatted(b));
  }
}
