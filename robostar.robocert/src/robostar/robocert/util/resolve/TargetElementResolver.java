package robostar.robocert.util.resolve;

import circus.robocalc.robochart.NamedElement;
import robostar.robocert.ControllerTarget;
import robostar.robocert.InControllerTarget;
import robostar.robocert.InModuleTarget;
import robostar.robocert.ModuleTarget;
import robostar.robocert.OperationTarget;
import robostar.robocert.StateMachineTarget;
import robostar.robocert.Target;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Resolves the elements of RoboCert targets.
 *
 * @author Matt Windsor
 */
public class TargetElementResolver extends RoboCertSwitch<NamedElement> {
  /* This used to be an innate derived attribute of Target, but implementing it on the metamodel
  side (which has poor support for inheritance of derived attributes) involved overriding the code
  with custom implementations in a way that is flimsy when exposed to Maven builds.  As such, we now
  do it here. */

  // NOTE: add any new Targets as they are defined.

  /**
   * Resolves the underlying element of a target.
   *
   * @param t the target to inspect.
   * @return the named element corresponding to the target (either the component of a component
   * target, or the container of a collection target).
   */
  public NamedElement resolve(Target t) {
    final var elem = doSwitch(t);

    // Safety valve in case we forget to add an override.
    if (elem == null) {
      throw new UnsupportedOperationException(
          "Tried to resolve the element of a target %s that is not yet supported.  This is an internal error.".formatted(
              t));
    }

    return elem;
  }

  //
  // Collection targets
  //

  @Override
  public NamedElement caseInModuleTarget(InModuleTarget t) {
    return t.getModule();
  }

  @Override
  public NamedElement caseInControllerTarget(InControllerTarget t) {
    return t.getController();
  }

  //
  // Component targets
  //

  @Override
  public NamedElement caseModuleTarget(ModuleTarget t) {
    return t.getModule();
  }

  @Override
  public NamedElement caseControllerTarget(ControllerTarget t) {
    return t.getController();
  }

  @Override
  public NamedElement caseStateMachineTarget(StateMachineTarget t) {
    return t.getStateMachine();
  }

  @Override
  public NamedElement caseOperationTarget(OperationTarget t) {
    return t.getOperation();
  }
}
