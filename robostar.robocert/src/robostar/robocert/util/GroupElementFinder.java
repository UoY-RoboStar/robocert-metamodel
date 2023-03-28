/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util;

import java.util.Optional;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.Actor;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;
import robostar.robocert.MessageOccurrence;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;
import robostar.robocert.util.resolve.ResolveHelper;

/**
 * Abstract base class for finders of parts of specification groups.
 *
 * <p>
 * The idea of this class and its descendants is to factor out a large amount of either manual
 * (safe) or reflective (unsafe) object graph traversal.  Each {@code findOnX} method that accepts a
 * specific object class is safe: it will follow a known path from the object to its enclosing
 * group, and will only return empty if an element on the path is empty.
 *
 * <p>
 * The {@code findOnObject} method, will fall back to performing container traversal if it doesn't
 * recognise the class of object presented to it, and is therefore unsafe.
 *
 * @param <T> type of group element being found (e.g. {@link SpecificationGroup} or {@link Target})
 * @author Matt Windsor
 */
public abstract class GroupElementFinder<T> {

  /**
   * Finds the group element of an arbitrary EObject.
   *
   * @param e the object in question
   * @return the group either directly containing e, or associated to it through the object graph
   */
  public Optional<T> findOnObject(EObject e) {
    return new RoboCertSwitch<Optional<T>>() {
      @Override
      public Optional<T> defaultCase(EObject e) {
        // Fall back to traversing containments.
        // We don't check for a container of type T, because it is likely that the T is either
        // SpecificationGroup *or* a sibling of one of e's containers rather than its parent.
        return ResolveHelper.containerOfType(e, SpecificationGroup.class).flatMap(this::caseSpecificationGroup);
      }

      @Override
      public Optional<T> caseSpecificationGroup(SpecificationGroup g) {
        return findOnGroup(g);
      }

      @Override
      public Optional<T> caseTarget(Target t) {
        return findOnTarget(t);
      }

      @Override
      public Optional<T> caseActor(Actor a) {
        return findOnActor(a);
      }

      @Override
      public Optional<T> caseInteraction(Interaction i) {
        return findOnInteraction(i);
      }

      @Override
      public Optional<T> caseLifeline(Lifeline l) {
        return findOnLifeline(l);
      }

      @Override
      public Optional<T> caseMessageOccurrence(MessageOccurrence e) {
        return findOnMessageOccurrence(e);
      }
    }.doSwitch(e);
  }

  /**
   * Finds the element on a specification group.
   *
   * @param g the group
   * @return the group's element, if one exists
   */
  public abstract Optional<T> findOnGroup(SpecificationGroup g);

  /**
   * Finds the element on a target.
   *
   * @param t the target
   * @return the target's group element, if one exists
   */
  public Optional<T> findOnTarget(Target t) {
    return Optional.ofNullable(t).map(Target::getGroup).flatMap(this::findOnGroup);
  }

  /**
   * Finds the element on an actor.
   *
   * @param a the actor
   * @return the actor's group element, if one exists
   */
  public Optional<T> findOnActor(Actor a) {
    return Optional.ofNullable(a).map(Actor::getGroup).flatMap(this::findOnGroup);
  }

  /**
   * Finds the element on a lifeline.
   *
   * @param l the lifeline
   * @return the lifeline's group element, if one exists
   */
  public Optional<T> findOnLifeline(Lifeline l) {
    // We could also traverse through the lifeline's actor, but this would result in some
    // traversal methods going up the object graph and others going down it, which would be
    // inconsistent.  (It also complicates checking for well-formedness conditions: for instance,
    // the one where we are checking that a lifeline's actor is on the same group as its
    // interaction).
    return Optional.ofNullable(l).map(Lifeline::getParent).flatMap(this::findOnInteraction);
  }

  /**
   * Finds the element on an interaction.
   *
   * @param i the interaction
   * @return the interaction's group element, if one exists
   */
  public Optional<T> findOnInteraction(Interaction i) {
    return Optional.ofNullable(i).map(Interaction::getGroup).flatMap(this::findOnGroup);
  }

  /**
   * Finds the element on a message occurrence.
   *
   * @param e the message occurrence
   * @return the occurrence's group element, if one exists
   */
  public Optional<T> findOnMessageOccurrence(MessageOccurrence e) {
    return Optional.ofNullable(e).map(MessageOccurrence::getActor).flatMap(this::findOnActor);
  }
}
