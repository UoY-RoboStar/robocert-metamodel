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
import com.google.inject.Inject;

import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import robostar.robocert.Actor;
import robostar.robocert.ComponentActor;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.Target;
import robostar.robocert.TargetActor;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Resolves actors into the connection nodes that can represent them.
 *
 * @param tgtRes       resolves targets to connection nodes.
 */
public record ActorNodeResolver(TargetNodeResolver tgtRes) {

  /**
   * Constructs an actor resolver.
   *
   * @param tgtRes       resolves targets to connection nodes.
   */
  @Inject
  public ActorNodeResolver {
    Objects.requireNonNull(tgtRes);
  }

  /**
   * Resolves a message occurrence to a stream of connection nodes that can represent its actor.
   *
   * @param occ    the occurrence to resolv
   * @param target the target, used if the actor is a {@link TargetActor}
   * @return a stream of connection nodes that can represent this actor
   */
  public Stream<ConnectionNode> resolveInOccurrence(MessageOccurrence occ, Target target) {
    return Stream.ofNullable(occ.getActor()).flatMap(a -> resolve(a, target));
  }

  /**
   * Resolves an actor to a stream of connection nodes that can represent that actor.
   *
   * <p>The stream may contain more than one node in two situations: either the actor is a target
   * actor and the target is a module (in which case, the module's non-platform components stand in
   * for the module), or the actor is a world (in which case, any of the parent's connection nodes
   * can appear).
   *
   * @param actor  the actor to resolve
   * @param target the target, used if the actor is a {@link TargetActor}
   * @return a stream of connection nodes that can represent this actor
   */
  public Stream<ConnectionNode> resolve(Actor actor, Target target) {
    return new RoboCertSwitch<Stream<ConnectionNode>>() {
      @Override
      public Stream<ConnectionNode> defaultCase(EObject object) {
        throw new IllegalArgumentException("can't resolve actor %s".formatted(actor));
      }

      @Override
      public Stream<ConnectionNode> caseComponentActor(ComponentActor c) {
        return Stream.of(c.getNode());
      }

      @Override
      public Stream<ConnectionNode> caseTargetActor(TargetActor t) {
        return tgtRes.resolve(target);
      }
    }.doSwitch(actor);
  }
}
