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
 * Tests any custom functionality on GapMessageSets.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class GapMessageSetImplCustomTest {
	@Inject extension MessageSpecFactory
	
	@Test def void testTarget() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).target.expectTarget
		intEvent.topic.gapSpec(MessageDirection::INBOUND).target.expectTarget		
	}
	
	@Test def void testFrom() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).from.expectTarget
		intEvent.topic.gapSpec(MessageDirection::INBOUND).from.expectWorld
	}
	
	@Test def void testTo() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).to.expectWorld
		intEvent.topic.gapSpec(MessageDirection::INBOUND).to.expectTarget
	}

}
