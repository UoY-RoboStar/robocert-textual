package robocalc.robocert.tests.util

import robocalc.robocert.tests.RoboCertInjectorProvider

/**
 * An extended injector provider that slipstreams in some test utility
 * bindings.
 */
class RoboCertCustomInjectorProvider extends RoboCertInjectorProvider {
	override internalCreateInjector() {
		super.internalCreateInjector.createChildInjector(new RoboCertCustomModule)
	}
}