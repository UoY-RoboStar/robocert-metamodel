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
 * @param certFac the underlying RoboCert factory
 * @author Matt Windsor
 */
public record MessageFactory(RoboCertFactory certFac) {

  /**
   * The default message factory.
   */
  public static MessageFactory DEFAULT = new MessageFactory(RoboCertFactory.eINSTANCE);

  @Inject
  public MessageFactory {
    Objects.requireNonNull(certFac);
  }

  /**
   * Constructs a new endpoint for a given actor.
   *
   * @param a actor in question.
   * @return a wrapping of {@code a} in an endpoint.
   */
  public MessageOccurrence actor(Actor a) {
    final var e = certFac.createMessageOccurrence();
    e.setActor(a);
    return e;
  }

  /**
   * Constructs a new gate endpoint.
   *
   * @return a gate endpoint
   */
  public Gate gate() {
    return certFac.createGate();
  }

  /**
   * Constructs a message with the given topic, edge, and arguments.
   *
   * @param from  from-end to use for the message spec.
   * @param to    to-end to use for the message spec.
   * @param topic topic to use for the message spec.
   * @param args  arguments to use for the message spec.
   * @return the specification.
   */
  public Message message(MessageEnd from, MessageEnd to, MessageTopic topic,
      ValueSpecification... args) {
    return message(from, to, topic, Arrays.asList(args));
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
  public Message message(MessageEnd from, MessageEnd to, MessageTopic topic,
      Collection<? extends ValueSpecification> args) {
    final var it = certFac.createMessage();
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
    final var it = certFac.createEventTopic();
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
    final var it = certFac.createOperationTopic();
    it.setOperation(o);
    return it;
  }

  /**
   * Constructs a message fragment with the default temperature.
   *
   * @param m message to wrap in a fragment.
   * @return constructed message fragment.
   */
  public MessageFragment fragment(Message m) {
    final var frag = certFac.createMessageFragment();
    frag.setMessage(m);
    return frag;
  }

  /**
   * Constructs a message fragment with the given temperature.
   *
   * @param m    message to wrap in a fragment.
   * @param temp temperature of the message fragment.
   * @return constructed message fragment.
   */
  public MessageFragment fragment(Message m, Temperature temp) {
    final var frag = fragment(m);
    frag.setTemperature(temp);
    return frag;
  }
}
