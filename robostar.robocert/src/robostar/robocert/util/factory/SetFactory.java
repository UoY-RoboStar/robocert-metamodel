/*
 * Copyright (c) 2021, 2022, 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.util.factory;

import com.google.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import robostar.robocert.BinaryMessageSet;
import robostar.robocert.BinarySetOperator;
import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.Message;
import robostar.robocert.MessageSet;
import robostar.robocert.NamedMessageSet;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.UniverseMessageSet;

/**
 * Contains utility methods for constructing sets.
 *
 * @author Matt Windsor
 */
public record SetFactory(RoboCertFactory rf) {

  /**
   * The default set factory.
   */
  public static SetFactory DEFAULT = new SetFactory(RoboCertFactory.eINSTANCE);

  /**
   * Constructs a set factory.
   *
   * @param rf the underlying RoboCert factory.
   */
  @Inject
  public SetFactory {
    Objects.requireNonNull(rf);
  }

  /**
   * Creates a named message set.
   *
   * @param name the name of the message set
   * @param set  the underlying message set
   * @return the named message set
   */
  public NamedMessageSet named(String name, MessageSet set) {
    final var it = rf.createNamedMessageSet();
    it.setName(name);
    it.setSet(set);
    return it;
  }

  /**
   * @param elements the contents to put into the set
   * @return an extensional set with the given contents
   */
  public ExtensionalMessageSet extensional(Message... elements) {
    return extensional(List.of(elements));
  }

  /**
   * @param elements the contents to put into the set.
   * @return an extensional set with the given contents.
   */
  public ExtensionalMessageSet extensional(Collection<Message> elements) {
    final var it = rf.createExtensionalMessageSet();
    it.getMessages().addAll(elements);
    return it;
  }

  /**
   * @param element the content to put into the set.
   * @return a singleton extensional set with the given element.
   */
  public ExtensionalMessageSet singleton(Message element) {
    final var it = rf.createExtensionalMessageSet();
    it.getMessages().add(element);
    return it;
  }

  /**
   * @return a universe set.
   */
  public UniverseMessageSet universe() {
    return rf.createUniverseMessageSet();
  }

  /**
   * @return an empty set.
   */
  public ExtensionalMessageSet empty() {
    return rf.createExtensionalMessageSet();
  }

  /**
   * @param l the left-hand side set.
   * @param r the right-hand side set.
   * @return a set representing the union of the two sets.
   */
  public BinaryMessageSet union(MessageSet l, MessageSet r) {
    return binary(l, BinarySetOperator.UNION, r);
  }

  /**
   * @param l the left-hand side set.
   * @param r the right-hand side set.
   * @return a set representing the intersection of the two sets.
   */
  public BinaryMessageSet inter(MessageSet l, MessageSet r) {
    return binary(l, BinarySetOperator.INTERSECTION, r);
  }

  /**
   * @param l the left-hand side set.
   * @param r the right-hand side set.
   * @return a set representing the difference of the LHS by the RHS.
   */
  public BinaryMessageSet diff(MessageSet l, MessageSet r) {
    return binary(l, BinarySetOperator.DIFFERENCE, r);
  }

  /**
   * @param l the left-hand side set.
   * @param o the operator.
   * @param r the right-hand side set.
   * @return a set representing the application of the operator to the two sets.
   */
  public BinaryMessageSet binary(MessageSet l, BinarySetOperator o, MessageSet r) {
    final var it = rf.createBinaryMessageSet();
    it.setLhs(l);
    it.setOperator(o);
    it.setRhs(r);
    return it;
  }
}