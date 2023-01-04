/*
 * Copyright (c) 2022, 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.Type;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.EventTopic;
import robostar.robocert.MessageTopic;
import robostar.robocert.OperationTopic;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Resolver for parameter types of message topics.
 *
 * @author Matt Windsor
 */
public class ParamTypeResolver {

  /**
   * Resolves the parameter types of a message topic.
   *
   * @param t the topic whose parameter types are wanted.
   * @return a stream of types, in the same order as the parameters of the topic.
   */
  public Stream<Type> resolve(MessageTopic t) {
    return new RoboCertSwitch<Stream<Type>>() {

      @Override
      public Stream<Type> defaultCase(EObject t) {
        throw new UnsupportedOperationException(
            "Tried to resolve the param types of a topic %s that is not yet supported.  This is an internal error.".formatted(
                t));
      }

      @Override
      public Stream<Type> caseEventTopic(EventTopic t) {
        // An event has one parameter iff it is a typed event (and that parameter has said type);
        // else, it has no parameter.
        return Stream.ofNullable(t.getEfrom().getType());
      }

      @Override
      public Stream<Type> caseOperationTopic(OperationTopic t) {
        return t.getOperation().getParameters().stream().map(Parameter::getType);
      }
    }.doSwitch(t);
  }
}
