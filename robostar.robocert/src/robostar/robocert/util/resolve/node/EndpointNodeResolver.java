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

import circus.robocalc.robochart.*;
import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Resolves endpoints into the connection nodes that can represent them.
 *
 * @param aNodeRes helper for resolving actors into connection nodes.
 * @param wNodeRes helper for resolving worlds into connection nodes.
 */
public record EndpointNodeResolver(ActorNodeResolver aNodeRes, WorldNodeResolver wNodeRes) {

  /**
   * Constructs an actor resolver.
   *
   * @param aNodeRes helper for resolving actors into connection nodes.
   * @param wNodeRes helper for resolving worlds into connection nodes.
   */
  @Inject
  public EndpointNodeResolver {
    Objects.requireNonNull(aNodeRes);
    Objects.requireNonNull(wNodeRes);
  }

  /**
   * Resolves an endpoint to a stream of connection nodes that can represent that endpoint.
   *
   * <p>The stream may contain more than one node in two situations: either the endpoint is a
   * target endpoint and the target is a module (in which case, the module's non-platform components
   * stand in for the module), or the endpoint is a world (in which case, any of the parent's
   * connection nodes can appear).
   *
   * @param endpoint the endpoint to resolve.  Must be attached to a specification group.
   * @return a stream of connection nodes that can represent this endpoint.
   */
  public Stream<ConnectionNode> resolve(Endpoint endpoint) {
    return new RoboCertSwitch<Stream<ConnectionNode>>() {
      @Override
      public Stream<ConnectionNode> defaultCase(EObject e) {
        throw new IllegalArgumentException("can't resolve endpoint %s".formatted(e));
      }

      @Override
      public Stream<ConnectionNode> caseActorEndpoint(ActorEndpoint e) {
        return aNodeRes.resolve(e.getActor());
      }

      @Override
      public Stream<ConnectionNode> caseWorld(World w) {
        return wNodeRes.resolve(w);
      }
    }.doSwitch(endpoint);
  }
}
