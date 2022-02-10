package robocalc.robocert.tests.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static robocalc.robocert.tests.util.IsStructurallyEqualTo.structurallyEqualTo;

import java.util.List;

import org.junit.jupiter.api.Test;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Abstract skeleton of tests that check target implementations.
 * 
 * @author Matt Windsor
 */
public abstract class TargetImplCustomTest<T extends Target> {
	/**
	 * The example used in the test; should be set up with a BeforeEach.
	 */
	protected T example;
	
	/**
	 * Tests that the string representation is correct.
	 */
	@Test
	void testToString() {
		assertThat(example.toString(), is(equalTo(expectedString())));
	}

	/**
	 * Tests that the components collection is what we expect it to
	 * be.
	 */
	@Test
	void testComponents() {
		// TODO(@MattWindsor91): this wrapper is a quickfix, consider replacing.
		if (example instanceof CollectionTarget t) {
			testListsEquivalent(expectedComponents(), t.getComponents());
		}
	}	
	
	/**
	 * Tests that the context elements collection is what we expect it to
	 * be.
	 */
	@Test
	void testContextElements() {
		testListsEquivalent(expectedContextElements(), example.getContextElements());
	}

	@Test
	void testElement() {
		final var expected = expectedElement();
		final var actual = example.getElement();
		
		assertThat(expected, is(notNullValue()));
		assertThat(expected, is(structurallyEqualTo(actual)));
	}

	/**
	 * @return the expected string representation.
	 */
	protected abstract String expectedString();
	
	/**
	 * @return the expected components from the example.
	 */
	protected abstract ConnectionNode[] expectedComponents();

	
	/**
	 * @return the expected context from the example.
	 */
	protected abstract NamedElement[] expectedContextElements();
	
	/**
	 * @return the expected element of the example.
	 */
	protected abstract NamedElement expectedElement();
	
	private <U> void testListsEquivalent(U[] expected, List<U>actual) {
		assertThat(actual.size(), is(expected.length));
		assertThat(actual, hasItems(expected));
	}

}