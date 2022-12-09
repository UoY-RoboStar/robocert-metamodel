package robostar.robocert.util;

import org.eclipse.emf.ecore.EObject;
import robostar.robocert.Actor;
import robostar.robocert.ActorEndpoint;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.Target;
import robostar.robocert.util.resolve.ResolveHelper;

import java.util.Optional;

/**
 * Helper class for finding the parent specification group of an element.
 *
 * <p>This class prefers to use direct links within the metamodel where
 * possible, but will fall back to performing a general EMF container check
 * if this isn't available.
 *
 * @author Matt Windsor
 */
public class GroupFinder {
    /**
     * Finds the parent group of an object.
     *
     * @param e object in question.
     * @return the group either directly containing e, or associated to it
     *         through the object graph.
     */
    public Optional<SpecificationGroup> find(EObject e) {
        return new RoboCertSwitch<Optional<SpecificationGroup>>() {
            @Override
            public Optional<SpecificationGroup> defaultCase(EObject e) {
                // Fall back to traversing containments.
                return ResolveHelper.containerOfType(e, SpecificationGroup.class);
            }

            @Override
            public Optional<SpecificationGroup> caseSpecificationGroup(SpecificationGroup g) {
                return Optional.ofNullable(g);
            }

            @Override
            public Optional<SpecificationGroup> caseTarget(Target t) {
                return Optional.ofNullable(t).flatMap(x -> caseSpecificationGroup(x.getGroup()));
            }

            @Override
            public Optional<SpecificationGroup> caseActor(Actor a) {
                return Optional.ofNullable(a).flatMap(x -> caseSpecificationGroup(x.getGroup()));
            }

            @Override
            public Optional<SpecificationGroup> caseActorEndpoint(ActorEndpoint e) {
                return Optional.ofNullable(e).flatMap(x -> caseActor(x.getActor()));
            }
        }.doSwitch(e);
    }

    /**
     * Finds the target of the parent group of an object.
     *
     * @param e object in question.
     * @return the group either directly containing e, or associated to it
     *         through the object graph.
     */
    public Optional<Target> findTarget(EObject e) {
        return find(e).flatMap(x -> Optional.ofNullable(x.getTarget()));
    }
}
