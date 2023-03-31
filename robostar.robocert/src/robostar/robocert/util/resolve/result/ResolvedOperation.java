/*
 * Copyright (c) 2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.result;

import java.util.Objects;
import robostar.robocert.MessageTopic;
import robostar.robocert.OperationTopic;
import robostar.robocert.util.resolve.EndIndex;

/**
 * Contains information about a resolved operation topic.
 *
 * <p>
 * At the moment, this doesn't store much information at all, but future expansions to RoboCert may
 * change this.
 *
 * @param op the operation itself
 * @author Matt Windsor
 */
public record ResolvedOperation(OperationTopic op) implements ResolvedTopic {

  public ResolvedOperation {
    Objects.requireNonNull(op);
  }

  @Override
  public MessageTopic topic() {
    return op;
  }

  @Override
  public EndIndex effectiveFrom() {
    // Currently, all operations go from a lifeline to a gate, so the effective-from is always from.
    return EndIndex.From;
  }
}
