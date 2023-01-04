package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Variable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;

/**
 * Resolves variables within interactions.
 *
 * @author Matt Windsor
 */
public class VariableResolver {

  /**
   * Gets a stream of all variables defined in an interaction.
   *
   * <p>
   * No order is guaranteed.
   *
   * @param seq interaction being investigated for variables.
   * @return a stream of variables, optionally accompanied by their lifelines.
   */
  public Stream<Result> resolve(Interaction seq) {
    // TODO(@MattWindsor91): non-lifeline variables
    if (seq == null) {
      return Stream.empty();
    }
    return allLifelineVars(seq.getLifelines());
  }

  private Stream<Result> allLifelineVars(List<Lifeline> lines) {
    if (lines == null) {
      return Stream.empty();
    }
    return lines.stream().flatMap(this::lifelineVars);
  }

  private Stream<Result> lifelineVars(Lifeline line) {
    if (line == null) {
      return Stream.empty();
    }

    final var vList = line.getVariables();
    if (vList == null) {
      return Stream.empty();
    }

    final var vars = vList.getVars();
    if (vars == null) {
      return Stream.empty();
    }

    return vars.stream().map(v -> new Result(v, Optional.of(line)));
  }

  /**
   * Wraps a resolved variable with its optional lifeline source.
   *
   * @param var      variable.
   * @param lifeline parent lifeline, if this came from one.
   */
  public record Result(Variable var, Optional<Lifeline> lifeline) {

  }
}
