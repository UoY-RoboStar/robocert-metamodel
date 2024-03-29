package robostar.robocert.util;

import circus.robocalc.robochart.And;
import circus.robocalc.robochart.BinaryExpression;
import circus.robocalc.robochart.BooleanExp;
import circus.robocalc.robochart.Different;
import circus.robocalc.robochart.Div;
import circus.robocalc.robochart.Expression;
import circus.robocalc.robochart.FloatExp;
import circus.robocalc.robochart.IntegerExp;
import circus.robocalc.robochart.InverseExp;
import circus.robocalc.robochart.LessOrEqual;
import circus.robocalc.robochart.Minus;
import circus.robocalc.robochart.Modulus;
import circus.robocalc.robochart.Mult;
import circus.robocalc.robochart.NamedExpression;
import circus.robocalc.robochart.Neg;
import circus.robocalc.robochart.Or;
import circus.robocalc.robochart.Plus;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.RoboChartFactory;
import com.google.inject.Inject;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Helper factory that uses a {@link RoboChartFactory} to produce specific types of expression.
 *
 * @param rc  the underlying RoboChart factory.
 *
 * @author Matt Windsor
 */
public record ExpressionFactory(RoboChartFactory rc) {

  @Inject
  public ExpressionFactory {
	  Objects.requireNonNull(rc);
  };

  /**
   * Creates a {@link BooleanExp} with the given truth value.
   *
   * @param truth the truth value.
   * @return a RoboCert lifting of the given truth value.
   */
  public BooleanExp bool(boolean truth) {
    final var x = rc.createBooleanExp();
    x.setValue(truth ? "true" : "false");
    return x;
  }

  /**
   * Creates a {@link FloatExp} with the given value.
   *
   * @param value the value.
   * @return a RoboCert lifting of the given float value.
   */
  public FloatExp floating(float value) {
    final var result = rc.createFloatExp();
    result.setValue(value);
    return result;
  }

  /**
   * Creates a {@link IntegerExp} with the given value.
   *
   * @param value the value.
   * @return a RoboCert lifting of the given integer value.
   */
  public IntegerExp integer(int value) {
    final var result = rc.createIntegerExp();
    result.setValue(value);
    return result;
  }

  /**
   * Creates a {@link RefExp} with a given named expression (eg a variable).
   *
   * @param v the named expression.
   * @return the reference expression.
   */
  public RefExp ref(NamedExpression v) {
    final var result = rc.createRefExp();
    result.setRef(v);
    return result;
  }

  /**
   * Creates a 'plus' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Plus plus(Expression lhs, Expression rhs) {
    return binary(rc::createPlus, lhs, rhs);
  }

  /**
   * Creates a 'minus' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Minus minus(Expression lhs, Expression rhs) {
    return binary(rc::createMinus, lhs, rhs);
  }

  /**
   * Creates a 'mult' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Mult mult(Expression lhs, Expression rhs) {
    return binary(rc::createMult, lhs, rhs);
  }

  /**
   * Creates a 'div' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Div div(Expression lhs, Expression rhs) {
    return binary(rc::createDiv, lhs, rhs);
  }

  /**
   * Creates a 'modulus' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Modulus modulus(Expression lhs, Expression rhs) {
    return binary(rc::createModulus, lhs, rhs);
  }

  /**
   * Creates an 'and' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public And and(Expression lhs, Expression rhs) {
    return binary(rc::createAnd, lhs, rhs);
  }

  /**
   * Creates an 'or' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Or or(Expression lhs, Expression rhs) {
    return binary(rc::createOr, lhs, rhs);
  }

  /**
   * Creates a '!=' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public Different diff(Expression lhs, Expression rhs) {
    return binary(rc::createDifferent, lhs, rhs);
  }

  /**
   * Creates a '<=' expression with the given operands.
   *
   * @param lhs the left operand.
   * @param rhs the right operand.
   * @return the given binary expression.
   */
  public LessOrEqual le(Expression lhs, Expression rhs) {
    return binary(rc::createLessOrEqual, lhs, rhs);
  }

  /**
   * Creates a binary expression with the given operands.
   *
   * @param ctor the constructor function.
   * @param lhs  the left operand.
   * @param rhs  the right operand.
   * @return the given binary expression.
   */
  private <T extends BinaryExpression> T binary(Supplier<T> ctor, Expression lhs, Expression rhs) {
    final var result = ctor.get();
    result.setLeft(lhs);
    result.setRight(rhs);
    return result;
  }

  /**
   * Creates an {@link InverseExp} with the given operand.
   *
   * @param e the operand.
   * @return the inverse expression.
   */
  public Neg neg(Expression e) {
    final var result = rc.createNeg();
    result.setExp(e);
    return result;
  }
}
