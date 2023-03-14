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

import circus.robocalc.robochart.Variable;
import java.util.Set;

/**
 * Contains various forms of variables detected on a fragment.
 *
 * @param inputs  any variables referenced by expressions on this fragment, without recursively
 *                considering its sub-fragments
 * @param outputs any variables bound by value specifications on this fragment, without recursively
 *                considering its sub-fragments
 * @author Matt Windsor
 */
public record FragmentVariableSet(Set<Variable> inputs, Set<Variable> outputs) {

}
