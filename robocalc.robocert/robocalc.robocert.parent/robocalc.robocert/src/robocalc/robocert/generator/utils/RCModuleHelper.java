/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 *
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import com.google.inject.Inject;
import java.util.stream.Stream;
import robocalc.robocert.model.robocert.util.DefinitionHelper;

/**
 * Helper for dealing with RoboChart modules.
 *
 * @author Matt Windsor
 */
public class RCModuleHelper {
  @Inject private DefinitionHelper dh;
  @Inject private CTimedGeneratorUtils gu;
  @Inject private ControllerHelper cx;

  /**
   * Gets the variables that make up this module's parameterisation.
   *
   * <p>This should align with the definition in the CSP semantics.
   *
   * @param it the RoboChart module
   * @return an iterator over (variable, container) pairs.
   */
  public Stream<Variable> parameterisation(RCModule it) {
    return Stream.concat(platformParams(it), controllerParams(it));
  }

  private Stream<Variable> platformParams(RCModule it) {
    return dh.platform(it).stream().flatMap(x -> gu.allLocalConstants(x).parallelStream());
  }

  private Stream<Variable> controllerParams(RCModule it) {
    return dh.controllers(it).flatMap(cx::moduleParameterisation);
  }
}
