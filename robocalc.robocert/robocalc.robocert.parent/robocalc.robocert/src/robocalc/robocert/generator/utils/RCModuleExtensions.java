/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 *
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils;

import java.util.stream.Stream;

import com.google.common.collect.Streams;
import com.google.inject.Inject;

import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import robocalc.robocert.model.robocert.util.DefinitionHelper;

/**
 * Extension methods for dealing with RoboChart modules.
 */
public class RCModuleExtensions {
	@Inject
	private DefinitionHelper dh;
	@Inject
	private CTimedGeneratorUtils gu;
	@Inject
	private ControllerExtensions cx;

	/**
	 * Gets the variables that make up this module's parameterisation.
	 *
	 * This should align with the definition in the CSP semantics.
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
		return dh.controllers(it).flatMap(x -> Streams.stream(cx.moduleParameterisation(x)));
	}

	/**
	 * Gets the contexts from the perspective of a RoboChart module.
	 *
	 * At time of writing, these are robotic platform only.
	 *
	 * @param it the RoboChart module.
	 * @return an iterator over the contexts accessible to this module.
	 */
	public Stream<Context> contexts(RCModule it) {
		return dh.platform(it).map(this::id).stream();
	}

	private Context id(RoboticPlatformDef x) {
		return x;
	}
}
