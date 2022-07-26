package robostar.robocert.textual.tests.model;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static robostar.robocert.textual.tests.util.IsStructurallyEqualTo.structurallyEqualTo;

import com.google.inject.Inject;
import java.util.List;

import org.junit.jupiter.api.Test;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.NamedElement;
import robostar.robocert.CollectionTarget;
import robostar.robocert.Target;
import robostar.robocert.util.resolve.TargetComponentsResolver;
import robostar.robocert.util.resolve.TargetElementResolver;

/**
 * Abstract skeleton of tests that check target implementations and resolution of their elements
 * and components.
 * 
 * @author Matt Windsor
 */
public abstract class TargetTest<T extends Target> {
	/**
	 * The example used in the test; should be set up with a BeforeEach.
	 */
	protected T example;

	@Inject
	private TargetElementResolver elemRes;

	@Inject
	private TargetComponentsResolver compRes;
	
	/**
	 * Tests that the string representation is correct.
	 */
	@Test
	void testToString() {
		assertThat(example.toString(), is(equalTo(expectedString())));
	}

	/**
	 * Tests that the components collection is what we expect it to be.
	 */
	@Test
	void testComponents() {
		testListsEquivalent(expectedComponents(), components());
	}

	private List<ConnectionNode> components() {
		if (example instanceof CollectionTarget t) {
			return compRes.resolve(t).toList();
		}
		return List.of();
	}

	@Test
	void testElement() {
		final var expected = expectedElement();
		final var actual = elemRes.resolve(example);
		
		assertThat(expected, is(notNullValue()));
		assertThat(expected, is(structurallyEqualTo(actual)));
	}

	/**
	 * Gets the expected string representation.
	 * @return the expected string.
	 */
	protected abstract String expectedString();
	
	/**
	 * Gets the expected components from the example.
	 * @return the expected components.
	 */
	protected abstract ConnectionNode[] expectedComponents();

	/**
	 * Gets the expected element of the example.
	 * @return the expected element.
	 */
	protected abstract NamedElement expectedElement();
	
	private <U> void testListsEquivalent(U[] expected, List<U>actual) {
		assertThat("lists must have same number of items", actual.size(), is(expected.length));
		assertThat(actual, hasItems(expected));
	}

}
