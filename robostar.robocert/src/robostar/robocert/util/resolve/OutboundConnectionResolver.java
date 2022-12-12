package robostar.robocert.util.resolve;

import circus.robocalc.robochart.*;
import com.google.inject.Inject;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.*;
import robostar.robocert.util.RoboCertSwitch;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Resolves 'outbound' connections between a target and its world.
 *
 * @param modRes  module aspect resolver.
 * @param ctrlRes controller aspect resolver.
 * @param stmRes  state machine aspect resolver.
 * @param defRes  definition resolver.
 */
public record OutboundConnectionResolver(ModuleResolver modRes, ControllerResolver ctrlRes, StateMachineResolver stmRes,
                                         DefinitionResolver defRes) {
    @Inject
    public OutboundConnectionResolver {
        Objects.requireNonNull(modRes);
        Objects.requireNonNull(ctrlRes);
        Objects.requireNonNull(stmRes);
        Objects.requireNonNull(defRes);
    }

    /**
     * Gets the stream of connections that go from this target to its world.
     *
     * @param target the target whose connections should be enumerated.
     * @return the stream of outbound connections.
     */
    public Stream<Connection> resolve(Target target) {
        return new RoboCertSwitch<Stream<Connection>>() {
            @Override
            public Stream<Connection> defaultCase(EObject e) {
                throw new IllegalArgumentException("can't get outbound connections for target %s".formatted(e));
            }

            @Override
            public Stream<Connection> caseHasModuleTarget(HasModuleTarget t) {
                // We consider the connections from module elements to the platform to be 'outer', here.
                return modRes.outboundConnections(t.getModule());
            }

            @Override
            public Stream<Connection> caseHasControllerTarget(HasControllerTarget t) {
                // An outbound controller connection is any connection in the module that goes to or from the
                // controller.
                final var ctrl = t.getController();
                final var mod = ctrlRes.module(ctrl);
                return mod.stream().flatMap(m -> m.getConnections().stream()).filter(c -> connectsController(c, ctrl));
            }

            @Override
            public Stream<Connection> caseStateMachineTarget(StateMachineTarget t) {
                return outboundStateMachineBodyConnections(t.getStateMachine());
            }

            @Override
            public Stream<Connection> caseOperationTarget(OperationTarget t) {
                return outboundStateMachineBodyConnections(t.getOperation());
            }
        }.doSwitch(target);
    }

    private Stream<Connection> outboundStateMachineBodyConnections(StateMachineBody smb) {
        return stmRes.controller(smb).stream().flatMap(c -> c.getConnections().stream()).filter(c -> connectsStateMachine(c, smb));
    }

    private boolean connectsController(Connection c, ControllerDef ctrl) {
        return connectsController(c.getFrom(), ctrl) || connectsController(c.getTo(), ctrl);
    }

    private boolean connectsController(ConnectionNode n, ControllerDef ctrl) {
        return defRes.normalise(n) == ctrl;
    }

    private boolean connectsStateMachine(Connection c, StateMachineBody smb) {
        return connectsStateMachine(c.getFrom(), smb) || connectsStateMachine(c.getTo(), smb);
    }

    private boolean connectsStateMachine(ConnectionNode n, StateMachineBody smb) {
        return defRes.normalise(n) == smb;
    }
}
