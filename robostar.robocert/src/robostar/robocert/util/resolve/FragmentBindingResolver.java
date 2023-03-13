/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Variable;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.InteractionFragment;
import robostar.robocert.MessageFragment;
import robostar.robocert.WildcardValueSpecification;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Resolves the variable bindings within an {@link InteractionFragment}.
 *
 * @author Matt Windsor
 */
public class FragmentBindingResolver {

  /**
   * Resolves the variable bindings within the given fragment.
   *
   * <p>
   * Presently, the only variable bindings are those within message value specifications.
   *
   * @param fragment the fragment being searched for variable bindings.
   * @return the stream of variable bindings within the given fragment.
   */
  public Stream<Variable> bindingsOf(InteractionFragment fragment) {
    return new RoboCertSwitch<Stream<Variable>>() {
      @Override
      public Stream<Variable> defaultCase(EObject object) {
        return Stream.empty();
      }

      @Override
      public Stream<Variable> caseMessageFragment(MessageFragment msg) {
        final var args = msg.getMessage().getArguments();
        return args.stream().mapMulti((arg, consumer) -> {
          if (arg instanceof WildcardValueSpecification wArg) {
            consumer.accept(wArg.getDestination());
          }
        });
      }
    }.doSwitch(fragment);
  }
}
