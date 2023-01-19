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

import circus.robocalc.robochart.Event;
import circus.robocalc.robochart.OperationSig;
import com.google.inject.Inject;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import robostar.robocert.*;

/**
 * High-level factory for message-related objects.
 *
 * @param rc underlying RoboCert factory.
 * @author Matt Windsor
 */
public record MessageFactory(RoboCertFactory rc) {

  @Inject
  public MessageFactory {
    Objects.requireNonNull(rc);
  }

  /**
   * Constructs a new endpoint for a given actor.
   *
   * @param a actor in question.
   * @return a wrapping of {@code a} in an endpoint.
   */
  public ActorEndpoint actor(Actor a) {
    final var e = rc.createActorEndpoint();
    e.setActor(a);
    return e;
  }

  /**
   * Constructs a new endpoint for the world.
   *
   * @return a world endpoint.
   */
  public World world() {
    return rc.createWorld();
  }

  /**
   * Constructs a message spec with the given topic, edge, and arguments.
   *
   * @param from  from-actor to use for the message spec.
   * @param to    to-actor to use for the message spec.
   * @param topic topic to use for the message spec.
   * @param args  arguments to use for the message spec.
   * @return the specification.
   */
  public Message spec(Endpoint from, Endpoint to, MessageTopic topic, ValueSpecification... args) {
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
  public Message spec(Endpoint from, Endpoint to, MessageTopic topic,
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
    final var it = eventTopic(efrom);
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
   * Creates a target actor.
   *
   * @param name the name of the target actor
   *
   * @return a target actor
   */
  public TargetActor targetActor(String name) {
    final var a = rc.createTargetActor();
    a.setName(name);
    return a;
  }

  /**
   * Constructs a message fragment with the default temperature.
   * @param m message to wrap in a fragment.
   * @return constructed message fragment.
   */
  public MessageFragment fragment(Message m) {
    final var frag = rc.createMessageFragment();
    frag.setMessage(m);
    return frag;
  }

  /**
   * Constructs a message fragment with the given temperature.
   * @param m message to wrap in a fragment.
   * @param temp temperature of the message fragment.
   * @return constructed message fragment.
   */
  public MessageFragment fragment(Message m, Temperature temp) {
    final var frag = fragment(m);
    frag.setTemperature(temp);
    return frag;
  }
}
