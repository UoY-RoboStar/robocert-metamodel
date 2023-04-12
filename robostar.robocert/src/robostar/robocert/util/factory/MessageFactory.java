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

import java.util.Collection;
import java.util.List;
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
   * Constructs a new message occurrence.
   *
   * @param a the actor being occurred upon
   * @return a wrapping of {@code a} in a message occurrence
   */
  public MessageOccurrence occurrence(Actor a) {
    final var e = certFac.createMessageOccurrence();
    e.setActor(a);
    return e;
  }

  /**
   * Constructs a new gate.
   *
   * @return a gate
   */
  public Gate gate() {
    return certFac.createGate();
  }

  /**
   * Constructs a message with the given topic, edge, and arguments.
   *
   * @param from  the from-end to use for the message spec
   * @param to    the to-end to use for the message spec
   * @param topic the topic to use for the message spec
   * @param args  the arguments to use for the message spec
   * @return the resulting message
   */
  @Deprecated
  public Message message(MessageEnd from, MessageEnd to, MessageTopic topic,
      ValueSpecification... args) {
    return message(from, to, topic, List.of(args));
  }

  /**
   * Constructs a message spec with the given topic, edge, and argument collection.
   *
   * @param from  the from-end to use for the message spec
   * @param to    the to-end to use for the message spec
   * @param topic the topic to use for the message spec
   * @param args  the arguments to use for the message spec
   * @return the resulting message
   */
  @Deprecated
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

  /**
   * Starts building a (synchronous) operational message.
   *
   * @param op the operation of the message
   * @return a builder for the rest of the message
   */
  public Builder op(OperationSig op) {
    return sync(opTopic(op));
  }


  /**
   * Starts building an asynchronous single-event message.
   *
   * @param event the lone event of the message
   * @return a builder for the rest of the message
   */
  public Builder async(Event event) {
    return async(eventTopic(event));
  }

  /**
   * Starts building an asynchronous message.
   *
   * @param topic the topic of the message
   * @return a builder for the rest of the message
   */
  public Builder async(MessageTopic topic) {
    return message(topic, Synchronicity.ASYNCHRONOUS);
  }

  /**
   * Starts building a synchronous single-event message.
   *
   * @param event the lone event of the message
   * @return a builder for the rest of the message
   */
  public Builder sync(Event event) {
    return sync(eventTopic(event));
  }

  /**
   * Starts building a synchronous message.
   *
   * @param topic the topic of the message
   * @return a builder for the rest of the message
   */
  public Builder sync(MessageTopic topic) {
    return message(topic, Synchronicity.SYNCHRONOUS);
  }

  /**
   * Starts building a message.
   *
   * @param topic         the topic of the message
   * @param synchronicity the synchronicity of the message
   * @return a builder for the rest of the message
   */
  public Builder message(MessageTopic topic, Synchronicity synchronicity) {
    return new Builder(topic, synchronicity);
  }

  /**
   * A base builder for messages.
   *
   * @author Matt Windsor
   */
  public class Builder {

    private final MessageTopic topic;
    private final Synchronicity synchronicity;

    private Builder(MessageTopic topic, Synchronicity synchronicity) {
      this.topic = Objects.requireNonNull(topic, "topic must not be null");
      this.synchronicity = Objects.requireNonNull(synchronicity, "sync must not be null");
    }

    /**
     * @return a builder for messages from a Gate
     */
    public From fromGate() {
      return from(gate());
    }

    /**
     * Begins construction of a message originating from the given actor.
     *
     * @param actor the actor to use
     * @return a builder for messages from an occurrence over the given actor
     */
    public From from(Actor actor) {
      return from(occurrence(actor));
    }

    /**
     * Begins construction of a message originating from the given actor.
     *
     * @param end the from-end to use
     * @return a builder for messages from the given end
     */
    public From from(MessageEnd end) {
      return new From(topic, synchronicity, end);
    }
  }

  /**
   * A builder stage for messages that corresponds to the from-end being fixed.
   *
   * @author Matt Windsor
   */
  public class From {

    private final MessageTopic topic;
    private final Synchronicity synchronicity;
    private final MessageEnd from;

    private From(MessageTopic topic, Synchronicity synchronicity, MessageEnd from) {
      this.topic = Objects.requireNonNull(topic, "topic must not be null");
      this.synchronicity = Objects.requireNonNull(synchronicity, "sync must not be null");
      this.from = Objects.requireNonNull(from, "from-end must not be null");
    }

    /**
     * @return a builder for messages to a gate
     */
    public To toGate() {
      return to(gate());
    }

    /**
     * Continues building the message, with the given actor as the to-end.
     *
     * @param actor the to-actor to use
     * @return a builder for messages to an occurrence over the given actor
     */
    public To to(Actor actor) {
      return to(occurrence(actor));
    }

    /**
     * Continues building the message, with the given end as the to-end.
     *
     * @param end the to-end to use
     * @return a builder for messages to the given end
     */
    public To to(MessageEnd end) {
      return new To(certFac, topic, synchronicity, from, end);
    }
  }

  public class To extends AbstractRoboCertBuilder<To, Message> {

    private To(RoboCertFactory factory, MessageTopic topic, Synchronicity synchronicity,
        MessageEnd from, MessageEnd to) {
      this(factory, makeInitial(factory, topic, synchronicity, from, to));
    }

    private To(RoboCertFactory factory, Message message) {
      super(factory, message);
    }

    private static Message makeInitial(RoboCertFactory certFac, MessageTopic topic,
        Synchronicity synchronicity, MessageEnd from, MessageEnd to) {
      final var initial = certFac.createMessage();
      initial.setTopic(Objects.requireNonNull(topic, "topic must not be null"));
      initial.setSynchronicity(Objects.requireNonNull(synchronicity, "sync must not be null"));
      initial.setFrom(Objects.requireNonNull(from, "from-end must not be null"));
      initial.setTo(Objects.requireNonNull(to, "from-end must not be null"));

      return initial;
    }

    /**
     * Adds arguments to the message being built.
     *
     * @param args the arguments to add
     * @return a reference to this builder
     */
    public To arguments(ValueSpecification... args) {
      object.getArguments().addAll(List.of(args));

      return this;
    }

    /**
     * @return a fragment over the message being built and using the default temperature
     */
    public MessageFragment getFragment() {
      return fragment(get());
    }

    /**
     * Creates a fragment from this message with the given temperature.
     *
     * @param temp the temperature
     * @return a fragment over the message being built
     */
    public MessageFragment getFragment(Temperature temp) {
      return fragment(get(), temp);
    }

    @Override
    protected To selfWith(Message newObject) {
      return new To(certFactory, newObject);
    }

    @Override
    protected To self() {
      return this;
    }
  }
}
