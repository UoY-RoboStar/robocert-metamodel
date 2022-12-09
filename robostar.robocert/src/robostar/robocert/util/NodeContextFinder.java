/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.util;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import com.google.inject.Inject;

import java.util.Objects;

import robostar.robocert.ComponentActor;
import robostar.robocert.util.resolve.DefinitionResolver;

/**
 * Resolves RoboChart contexts related to connection nodes.
 *
 * @author Matt Windsor
 */
public record NodeContextFinder(DefinitionResolver defResolver) {

    /**
     * Constructs a node context finder.
     *
     * @param defResolver a definition helper used to find robotic platforms in modules.
     */
    @Inject
    public NodeContextFinder {
        Objects.requireNonNull(defResolver);
    }

    /**
     * Retrieves RoboChart contexts deriving from a {@link ComponentActor} attached to the given
     * component.
     *
     * @param n the component for which we are getting contexts.
     * @return the stream of contexts in scope of the actor.
     */
    public Context context(ConnectionNode n) {
        // Hypothesis: Either this node is a definition (and is therefore a context),
        // or it is a reference (and can be normalised to a context).
        if (!(defResolver.normalise(n) instanceof Context c)) {
            throw new IllegalArgumentException("Node not supported for context finding: %s".formatted(n));
        }
        return c;
    }
}
