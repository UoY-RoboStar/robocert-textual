package robocalc.robocert.tests.model

import com.google.inject.Inject
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.^extension.ExtendWith
import robocalc.robocert.model.robocert.MessageDirection
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import robocalc.robocert.tests.util.MessageSpecFactory

/**
 * Tests any custom functionality on ArrowMessageSets.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider)
class ArrowMessageSetImplCustomTest {
	@Inject extension MessageSpecFactory

	@Test def void testTarget() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).target.expectTarget
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).target.expectTarget
	}

	@Test def void testFrom() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).from.expectTarget
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).from.expectWorld
	}

	@Test def void testTo() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).to.expectWorld
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).to.expectTarget
	}
}
