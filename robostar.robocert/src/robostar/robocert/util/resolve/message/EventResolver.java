/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.util.resolve.message;

import java.util.stream.Stream;
import robostar.robocert.util.resolve.result.ResolvedEvent;

/**
 * Resolves an event topic to a connection.
 *
 * @author Matt Windsor
 */
public interface EventResolver {

  /**
   * Resolves an event to a candidate stream of connections.
   *
   * <p>This stream may contain zero, one, or many connections; typically anything other than one
   * is a well-formedness violation.
   *
   * @param query the resolver query
   * @return the stream of candidate connections alongside auxiliary information
   */
  Stream<ResolvedEvent> resolve(EventResolverQuery query);
}
