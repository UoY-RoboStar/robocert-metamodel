/*
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
 */
package robostar.robocert.util.resolve;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.RoboticPlatform;
import com.google.inject.Inject;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.util.EcoreUtil;
import robostar.robocert.CollectionTarget;
import robostar.robocert.InControllerTarget;
import robostar.robocert.InModuleTarget;
import robostar.robocert.Target;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Resolves the components of RoboCert collection targets.
 *
 * @author Matt Windsor
 */
public record TargetComponentsResolver(DefinitionResolver defRes) {

  /**
   * Constructs a target components resolver.
   *
   * @param defRes the definition resolver to use for inspecting controller components.
   */
  @Inject
  public TargetComponentsResolver {
    Objects.requireNonNull(defRes);
  }

  /*
   * Actions that require resolution
   */

  /**
   * Does the given target contain the given node?
   *
   * @param target    target to search.
   * @param component component for which we are searching.
   * @return true provided that target is a collection target and one of the components resolved by
   * this resolver is equal to the given component.
   */
  public boolean hasComponent(Target target, ConnectionNode component) {
    if (!(target instanceof CollectionTarget c)) {
      return false;
    }

    // As resolve() turns references into definitions, we need to do the same
    // to the component we're looking for; normalise() does this.
    final var def = defRes.normalise(component);

    return resolve(c).anyMatch(x -> EcoreUtil.equals(def, x));
  }

  /*
   * Resolution itself
   */

  /* This used to be an innate derived attribute of Target, but implementing it on the metamodel
  side (which has poor support for inheritance of derived attributes) involved overriding the code
  with custom implementations in a way that is flimsy when exposed to Maven builds.  As such, we now
  do it here. */

  /**
   * Resolves the underlying components of a collection target.
   *
   * @param t the target to inspect.
   * @return a stream of that target's components, as connection nodes.
   */
  public Stream<ConnectionNode> resolve(CollectionTarget t) {
    final var comps = new RoboCertSwitch<Stream<ConnectionNode>>() {
      @Override
      public Stream<ConnectionNode> caseInModuleTarget(InModuleTarget t) {
        return t.getModule().getNodes().stream().filter(x -> !(x instanceof RoboticPlatform));
      }

      @Override
      public Stream<ConnectionNode> caseInControllerTarget(InControllerTarget t) {
        final var ctrl = t.getController();
        return Stream.concat(ctrl.getLOperations().stream().map(defRes::resolve),
            ctrl.getMachines().stream().map(defRes::resolve));
      }
    }.doSwitch(t);

    // Safety valve in case we forget to add an override.
    if (comps == null) {
      throw new UnsupportedOperationException(
          "Tried to resolve the components of a target %s that is not yet supported.  This is an internal error.".formatted(
              t));
    }

    return comps;
  }
}
