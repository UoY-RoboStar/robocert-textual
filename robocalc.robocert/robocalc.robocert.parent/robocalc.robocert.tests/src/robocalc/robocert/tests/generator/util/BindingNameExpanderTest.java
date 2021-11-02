/**
 * 
 */
package robocalc.robocert.tests.generator.util;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robocalc.robocert.generator.utils.BindingNameExpander;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.MessageDirection;
import robocalc.robocert.model.robocert.RoboCertFactory;
import robocalc.robocert.tests.util.MessageSpecFactory;
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider;

/**
 * Tests for {@link BindingNameExpander}.
 * 
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertCustomInjectorProvider.class)
class BindingNameExpanderTest {
	@Inject private MessageSpecFactory msf;
	@Inject private RoboCertFactory rcf;
	@Inject private BindingNameExpander bx;
	
	/**
	 * Tests that getting the unambiguous name of a binding with no parent
	 * returns the name of that binding alone.
	 */
	@Test
	public void testGetUnambiguousName_NoParent() {
		var b = rcf.createBinding();
		b.setName("test");
		assertUnambiguousNameEqual("test", b);
	}

	/**
	 * Tests that getting the unambiguous name of a binding inside the
	 * root subsequence of a sequence diagram gets the expected name.
	 */
	@Test
	public void testGetUnambiguousName_RootSubsequence() {
		var w = msf.boundArg("test");
		var aspec = msf.arrowSpec(msf.topic(msf.intEvent()), MessageDirection.INBOUND, w);
		var aact = rcf.createArrowAction();
		aact.setBody(aspec);
		var astep = rcf.createActionStep();
		astep.setAction(aact);
		var ssq = rcf.createSubsequence();
		ssq.getSteps().add(astep);
		var sq = rcf.createSequence();
		sq.setBody(ssq);	
		
		assertUnambiguousNameEqual("step0_action_body_argument0", w.getBinding());
	}
	
	private void assertUnambiguousNameEqual(String expected, Binding b) {
		assertEquals(expected, bx.getUnambiguousName(b));
	}
}