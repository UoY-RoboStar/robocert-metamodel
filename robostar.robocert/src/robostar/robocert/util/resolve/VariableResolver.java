package robostar.robocert.util.resolve;

import circus.robocalc.robochart.Variable;
import java.util.List;
import java.util.stream.Stream;
import robostar.robocert.Interaction;
import robostar.robocert.Lifeline;
import robostar.robocert.util.resolve.result.LifelineVariableLocation;
import robostar.robocert.util.resolve.result.NoVariableLocation;
import robostar.robocert.util.resolve.result.ResolvedVariable;
import robostar.robocert.util.resolve.result.VariableLocation;

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
  public Stream<ResolvedVariable> resolve(Interaction seq) {
    // TODO(@MattWindsor91): non-lifeline variables
    if (seq == null) {
      return Stream.empty();
    }
    return allLifelineVars(seq.getLifelines());
  }

  private Stream<ResolvedVariable> allLifelineVars(List<Lifeline> lines) {
    if (lines == null) {
      return Stream.empty();
    }
    return lines.stream().flatMap(this::lifelineVars);
  }

  private Stream<ResolvedVariable> lifelineVars(Lifeline line) {
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

    return vars.stream().map(v -> new ResolvedVariable(v, new LifelineVariableLocation(line)));
  }

  /**
   * Tries to find information about a variable.
   *
   * @param var variable to resolve.
   * @return a resolver result containing {@code var} and any other known information about it.
   */
  public ResolvedVariable resolve(Variable var) {
    return new ResolvedVariable(var, location(var));
  }

  private VariableLocation location(Variable var) {
    // TODO(@MattWindsor91): other forms of location
    final var lifeline = ResolveHelper.containerOfType(var, Lifeline.class);
    return lifeline.map(x -> (VariableLocation) new LifelineVariableLocation(x))
        .orElse(new NoVariableLocation());
  }
}
