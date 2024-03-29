/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 */
package robostar.robocert.util.resolve;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.Operation;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.RoboticPlatformRef;
import circus.robocalc.robochart.StateMachine;
import circus.robocalc.robochart.StateMachineDef;
import circus.robocalc.robochart.StateMachineRef;
import circus.robocalc.robochart.util.RoboChartSwitch;

/**
 * Helper class for finding definitions of various RoboChart components.
 *
 * @author Matt Windsor
 */
public class DefinitionResolver {

  /**
   * If the node is a reference, dereference it.
   *
   * @param n the node to normalise.
   *
   * @return n if it is not a reference; the result of n.getRef() otherwise.
   */
  public ConnectionNode normalise(ConnectionNode n) {
    return new RoboChartSwitch<ConnectionNode>() {
      @Override
      public ConnectionNode caseConnectionNode(ConnectionNode n) {
        // return the node unchanged
        return n;
      }

      @Override
      public ConnectionNode caseStateMachine(StateMachine s) {
        return resolve(s);
      }

      @Override
      public ConnectionNode caseController(Controller c) {
        return resolve(c);
      }

      @Override
      public ConnectionNode caseOperation(Operation o) {
        return resolve(o);
      }

      @Override
      public ConnectionNode caseRoboticPlatform(RoboticPlatform r) {
        return resolve(r);
      }
    }.doSwitch(n);
  }

  /**
   * Resolves a {@link RoboticPlatform} into a {@link RoboticPlatformDef}.
   *
   * @param p the robotic platform to resolve.
   * @return the resolved platform.
   */
  public RoboticPlatformDef resolve(RoboticPlatform p) {
    if (p instanceof RoboticPlatformDef d) {
      return d;
    }
    if (p instanceof RoboticPlatformRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected RoboticPlatform{Def, Ref}, got %s".formatted(p));
  }

  /**
   * Resolves a {@link Controller} into a {@link ControllerDef}.
   *
   * @param c the controller to resolve.
   * @return the resolved operation.
   */
  public ControllerDef resolve(Controller c) {
    if (c instanceof ControllerDef d) {
      return d;
    }
    if (c instanceof ControllerRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected Controller{Def, Ref}, got %s".formatted(c));
  }

  /**
   * Resolves an {@link Operation} into an {@link OperationDef}.
   *
   * @param op the operation to resolve.
   * @return the resolved operation.
   */
  public OperationDef resolve(Operation op) {
    if (op instanceof OperationDef d) {
      return d;
    }
    if (op instanceof OperationRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected Operation{Def, Ref}, got %s".formatted(op));
  }

  /**
   * Resolves a {@link StateMachine} into an {@link StateMachineDef}.
   *
   * @param stm the state machine to resolve.
   * @return the resolved state machine.
   */
  public StateMachineDef resolve(StateMachine stm) {
    if (stm instanceof StateMachineDef d) {
      return d;
    }
    if (stm instanceof StateMachineRef r) {
      return r.getRef();
    }
    throw new IllegalArgumentException("expected StateMachine{Def, Ref}, got %s".formatted(stm));
  }
}
