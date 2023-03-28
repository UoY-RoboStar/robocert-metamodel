/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.wfc.seq;

import java.util.Objects;
import java.util.stream.Stream;
import javax.inject.Inject;
import robostar.robocert.Message;
import robostar.robocert.wfc.Checker;
import robostar.robocert.wfc.CheckerGroup;

/**
 * Performs well-formedness checking for the {@code SM} condition group.
 *
 * @author Matt Windsor
 */
public class MessageChecker extends CheckerGroup<Message> {
  private final MessageArgumentsChecker argCheck;

  @Inject
  public MessageChecker(MessageArgumentsChecker argCheck) {
    this.argCheck = Objects.requireNonNull(argCheck);
  }

  @Override
  protected Stream<Checker<Message>> checks() {
    return Stream.of(argCheck);
  }
}
