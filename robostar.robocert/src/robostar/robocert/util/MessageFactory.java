/*******************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/
package robostar.robocert.util;

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import robostar.robocert.Actor;
import robostar.robocert.EventTopic;
import robostar.robocert.Message;
import robostar.robocert.MessageTopic;
import robostar.robocert.OperationTopic;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.TargetActor;
import robostar.robocert.ValueSpecification;
import robostar.robocert.World;

/**
 * High-level factory for message-related objects.
 *
 * @param rc  underlying RoboCert factory.
 *
 * @author Matt Windsor
 */
public record MessageFactory(RoboCertFactory rc) {

  @Inject
  public MessageFactory {
	  Objects.requireNonNull(rc);
  };

  /**
   * Constructs a message spec with the given topic, edge, and arguments.
   *
   * @param from  from-actor to use for the message spec.
   * @param to    to-actor to use for the message spec.
   * @param topic topic to use for the message spec.
   * @param args  arguments to use for the message spec.
   * @return the specification.
   */
  public Message spec(Actor from, Actor to, MessageTopic topic, ValueSpecification... args) {
    return spec(from, to, topic, Arrays.asList(args));
  }

  /**
   * Constructs a message spec with the given topic, edge, and argument collection.
   *
   * @param from  from-actor to use for the message spec.
   * @param to    to-actor to use for the message spec.
   * @param topic the topic to use for the message spec.
   * @param args  the arguments to use for the message spec.
   * @return the specification.
   */
  public Message spec(Actor from, Actor to, MessageTopic topic,
      Collection<? extends ValueSpecification> args) {
    final var it = rc.createMessage();
    it.setFrom(from);
    it.setTo(to);
    it.setTopic(topic);
    it.getArguments().addAll(args);
    return it;
  }

  /**
   * Constructs an event topic with the given from-event.
   *
   * @param e the event to use.
   * @return the event topic.
   */
  public EventTopic eventTopic(Event e) {
    final var it = rc.createEventTopic();
    it.setEfrom(e);
    return it;
  }

  /**
   * Constructs an event topic with the given from-event and to-event.
   *
   * @param efrom the from-event to use.
   * @param eto   the to-event to use.
   * @return the event topic.
   */
  public EventTopic eventTopic(Event efrom, Event eto) {
    final var it = rc.createEventTopic();
    it.setEfrom(efrom);
    it.setEto(eto);
    return it;
  }

  /**
   * Constructs an operation topic with the given operation.
   *
   * @param o the signature of the operation to use.
   * @return the event topic.
   */
  public OperationTopic opTopic(OperationSig o) {
    final var it = rc.createOperationTopic();
    it.setOperation(o);
    return it;
  }

  /**
   * @return a target actor.
   */
  public TargetActor targetActor() {
    return rc.createTargetActor();
  }

  /**
   * @return a world.
   */
  public World world() {
    return rc.createWorld();
  }

  /**
   * @return a list containing all actors defined on a module target.
   */
  public List<Actor> systemActors() {
    return List.of(targetActor(), world());
  }
}
