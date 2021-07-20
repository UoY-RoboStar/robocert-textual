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
 * Tests any custom functionality on MessageSpecs.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class MessageSpecImplCustomTest {
	@Inject extension MessageSpecFactory
	
	/**
	 * Tests we can get the target of a properly contained spec inside a gap.
	 */
	@Test def void testGapTarget() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).target.expectTarget
		intEvent.topic.gapSpec(MessageDirection::INBOUND).target.expectTarget		
	}
	
	/**
	 * Tests we can get the source of a properly contained spec inside a gap.
	 */
	@Test def void testGapFrom() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).from.expectTarget
		intEvent.topic.gapSpec(MessageDirection::INBOUND).from.expectWorld
	}
	
	/**
	 * Tests we can get the destination of a properly contained spec inside a gap.
	 */
	@Test def void testGapTo() {
		intEvent.topic.gapSpec(MessageDirection::OUTBOUND).to.expectWorld
		intEvent.topic.gapSpec(MessageDirection::INBOUND).to.expectTarget
	}

	/**
	 * Tests we can get the target of a properly contained spec inside an arrow.
	 */
	@Test def void testArrowTarget() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).target.expectTarget
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).target.expectTarget
	}

	/**
	 * Tests we can get the source of a properly contained spec inside an arrow.
	 */
	@Test def void testArrowFrom() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).from.expectTarget
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).from.expectWorld
	}

	/**
	 * Tests we can get the destination of a properly contained spec inside an arrow.
	 */
	@Test def void testArrowTo() {
		intEvent.topic.arrowSpec(MessageDirection::OUTBOUND).to.expectWorld
		intEvent.topic.arrowSpec(MessageDirection::INBOUND).to.expectTarget
	}
}
