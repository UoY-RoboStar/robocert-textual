/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.tests;

import com.google.inject.Inject;
import java.util.stream.Collectors;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
class RoboCertParsingTest {
	@Inject
	ParseHelper<robocalc.robocert.model.robocert.CertPackage> parseHelper;
	
	// TODO(@MattWindsor91): find out how on earth to use the validation helper here;
	// the problem is that it's impossible to construct a valid and meaningful
	// CertPackage that doesn't refer to things in an existing CertPackage.
	//
	// maybe inject them into the model post-facto?
	
	@Test
	void loadModel() {
		final var result = Assertions.assertDoesNotThrow(() -> parseHelper.parse("""
target module Mod
specification group S:
	actors = {target as T, world as W}
	sequence Test
		var x: real
		actors T and W
		anything until: deadlock on T
"""));
		Assertions.assertNotNull(result);
		final var errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors: %s".formatted(errors.stream().map(Object::toString).collect(
				Collectors.joining(", "))));
	}
}
