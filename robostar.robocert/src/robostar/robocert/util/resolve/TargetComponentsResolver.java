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
import java.util.Optional;
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
   * this resolver is equal to the given component modulo definition resolving.
   */
  public boolean hasComponent(Target target, ConnectionNode component) {
    if (!(target instanceof CollectionTarget c)) {
      return false;
    }

    // Equality by structural testing of the underlying definitions.
    final var def = defRes.normalise(component);

    return resolve(c).anyMatch(x -> EcoreUtil.equals(def, defRes.normalise(x)));
  }

  /**
   * Gets, if any, the component in the target that matches the one given.
   *
   * <p>This may be either the given component, or a dereference/reference thereof.
   *
   * @param target    target to search.
   * @param component component for which we are searching.
   * @return any component that is a subcomponent of the target and is equal to the given component
   * modulo definition resolving.
   */
  public Optional<ConnectionNode> find(Target target, ConnectionNode component) {
    if (!(target instanceof CollectionTarget c)) {
      return Optional.empty();
    }

    // As above.
    final var def = defRes.normalise(component);

    return resolve(c).filter(x -> EcoreUtil.equals(def, defRes.normalise(x))).findFirst();
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
        /* We do not resolve references to definitions.  This is because the CSP emitted at the
           RoboChart side does not (seemingly) do this resolution either.

           As such, equality/membership testing and other such operations need to be sensitive of
           whether they are getting a def or a ref. */
        final var ctrl = t.getController();
        return Stream.concat(ctrl.getLOperations().stream(), ctrl.getMachines().stream());
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
