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

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Context;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.Variable;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;

/**
 * Extension methods for dealing with RoboChart modules.
 */
class RCModuleExtensions {
	@Inject
	private ControllerExtensions cx;
	@Inject
	private CTimedGeneratorUtils gu;
	@Inject
	private RoboticPlatformExtensions px;

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
		return gu.allLocalConstants(platform(it)).parallelStream();
	}

	private Stream<Variable> controllerParams(RCModule it) {
		return controllers(it).flatMap(x -> Streams.stream(cx.moduleParameterisation(x)));
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
		return Stream.of(platform(it));
	}

	/**
	 * Gets the robotic platform definition for a RoboChart module.
	 *
	 * @param it the RoboChart module.
	 * @return the module's robotic platform.
	 */
	private RoboticPlatformDef platform(RCModule it) {
		return nodes(it, RoboticPlatform.class).map(px::definition).findFirst().get();
	}

	private Stream<ControllerDef> controllers(RCModule it) {
		return nodes(it, Controller.class).map(cx::definition);
	}

	private <T extends ConnectionNode> Stream<T> nodes(RCModule m, Class<T> clazz) {
		return m.getNodes().parallelStream().filter(clazz::isInstance).map(clazz::cast);
	}
}
