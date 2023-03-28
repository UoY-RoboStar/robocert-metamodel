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

import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.Type;
import com.google.common.collect.Streams;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.Message;
import robostar.robocert.ValueSpecification;
import robostar.robocert.WildcardValueSpecification;
import robostar.robocert.util.RoboCertSwitch;
import robostar.robocert.util.resolve.ParamTypeResolver;
import robostar.robocert.wfc.Checker;

/**
 * Performs well-formedness checking for the {@code SMA} condition group.
 *
 * @param typeChecker  checks expression types
 * @param paramTypeRes resolves parameter types
 * @author Matt Windsor
 */
public record MessageArgumentsChecker(ExpressionTypeChecker typeChecker,
                                      ParamTypeResolver paramTypeRes) implements Checker<Message> {

  @Inject
  public MessageArgumentsChecker {
    Objects.requireNonNull(paramTypeRes);
    Objects.requireNonNull(typeChecker);
  }

  @Override
  public boolean isValid(Message elem) {
    final Result result = check(elem);
    return result.isValid();
  }

  /**
   * Performs a check on the given message.
   *
   * @param elem the message to check
   * @return a {@link Result} containing the individual well-formedness condition results
   */
  public Result check(Message elem) {
    final var args = elem.getArguments();
    final var params = paramTypeRes.resolve(elem.getTopic()).toList();

    final var sma1 = isSMA1(args, params);
    final var sma2 = isSMA2(args, params);

    return new Result(sma1, sma2);
  }

  private static boolean isSMA1(EList<ValueSpecification> args, List<Type> params) {
    final var numArgs = args.size();
    final var numParams = params.size();
    return numParams == numArgs;
  }

  @SuppressWarnings("UnstableApiUsage")
  private boolean isSMA2(EList<ValueSpecification> args, List<Type> params) {
    return Streams.zip(args.stream(), params.stream(), this::argumentTypeOk).allMatch(t -> t);
  }

  private boolean argumentTypeOk(ValueSpecification arg, Type ptype) {
    return new RoboCertSwitch<Boolean>() {
      @Override
      public Boolean defaultCase(EObject ignored) {
        return Boolean.FALSE;
      }

      @Override
      public Boolean caseWildcardValueSpecification(WildcardValueSpecification ignored) {
        // Wildcards match against any type.
        return Boolean.TRUE;
      }

      @Override
      public Boolean caseExpressionValueSpecification(ExpressionValueSpecification e) {
        return typeChecker.checkType(e.getExpr(), ptype);
      }
    }.doSwitch(arg);
  }

  /**
   * Interface for things that can check expression types.
   *
   * <p>
   * An example is {@code RoboCalcTypeProvider} in the robochart-textual plugin.
   *
   * @author Matt Windsor
   */
  public interface ExpressionTypeChecker {

    /**
     * Is the expression type-compatible with the expected type?
     *
     * @param expr     the expression
     * @param expected the expected type
     * @return true provided that {@code expr} is type-compatible with {@code expected}
     */
    boolean checkType(Expression expr, Type expected);
  }

  /**
   * Result of well-formedness analysis for a message's arguments.
   *
   * @param isSMA1 whether the message satisfies {@code SMA1}: 'The arguments of a Message must be
   *               equal in size to the parameters of its topic'
   * @param isSMA2 whether the message satisfies {@code SMA2}: 'The arguments of a Message must be
   *               type-compatible with corresponding parameters'
   * @author Matt Windsor
   */
  public record Result(boolean isSMA1, boolean isSMA2) {

    /**
     * @return true provided that all checks passed.
     */
    public boolean isValid() {
      return isSMA1 && isSMA2;
    }
  }
}
