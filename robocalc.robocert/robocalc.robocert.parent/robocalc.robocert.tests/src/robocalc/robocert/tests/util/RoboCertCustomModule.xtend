package robocalc.robocert.tests.util

import com.google.inject.AbstractModule

class RoboCertCustomModule extends AbstractModule {
	override configure() {
		bind(MessageFactory)
	}
}