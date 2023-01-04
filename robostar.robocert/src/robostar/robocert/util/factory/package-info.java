/**
 * Abstractions over the RoboCert and RoboChart factories.
 *
 * <p>
 * While the EMF factories are straightforward to use in Xtend code, their
 * use in plain Java code involves a large amount of getter/setter
 * boilerplate to set up nested parameters of model elements.  The classes
 * in this package provide a higher-level abstraction, generally providing
 * single methods for constructing entire model objects in place.
 *
 * @author Matt Windsor
 */
package robostar.robocert.util.factory;