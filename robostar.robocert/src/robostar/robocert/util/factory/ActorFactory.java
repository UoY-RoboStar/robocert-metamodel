/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.factory;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.VariableModifier;
import com.google.inject.Inject;
import java.util.Objects;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.Lifeline;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.util.factory.robochart.VariableFactory;

/**
 * A factory for creating {@link Actor}s.
 *
 * @param certFac the underlying RoboCert factory
 * @param varFac  a variable factory, used for constructing lifelines
 * @author Matt Windsor
 */
public record ActorFactory(RoboCertFactory certFac, VariableFactory varFac) {

  /**
   * The default actor factory.
   */
  public static final ActorFactory DEFAULT = new ActorFactory(RoboCertFactory.eINSTANCE,
      VariableFactory.DEFAULT);


  @Inject
  public ActorFactory {
    Objects.requireNonNull(certFac);
  }

  /**
   * Creates a target actor.
   *
   * @param name the name of the target actor
   * @return a target actor
   */
  public TargetActor targetActor(String name) {
    final var a = certFac.createTargetActor();
    a.setName(name);
    return a;
  }

  /**
   * Creates a component actor.
   *
   * @param name the name of the component actor
   * @param node the component being referenced
   * @return a component actor
   */
  public ComponentActor componentActor(String name, ConnectionNode node) {
    final var a = certFac.createComponentActor();
    a.setName(name);
    a.setNode(node);
    return a;
  }

  /**
   * Creates a lifeline.
   *
   * @param a    the actor that the lifeline is representing
   * @param vars the variables defined on the lifeline
   * @return the created lifeline
   */
  public Lifeline lifeline(Actor a, Variable... vars) {
    final var l = certFac.createLifeline();
    l.setActor(a);
    l.setVariables(varFac.list(VariableModifier.VAR, vars));
    return l;
  }
}
