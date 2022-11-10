/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * generated by Xtext 2.25.0
 */
package robostar.robocert.textual.tests;

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
	ParseHelper<robostar.robocert.CertPackage> parseHelper;
	
	// TODO(@MattWindsor91): find out how on earth to use the validation helper here;
	// the problem is that it's impossible to construct a valid and meaningful
	// CertPackage that doesn't refer to things in an existing CertPackage.
	//
	// maybe inject them into the model post-facto?
	
	@Test
	void loadModel() {
		final var result = Assertions.assertDoesNotThrow(() -> parseHelper.parse("""
specification group S {
  target = module Mod
  actors = {target as T, world as W}
  sequence Test {
    var x: real
    actors T and W
    anything until deadlock on T end
  }
}
"""));
		Assertions.assertNotNull(result);
		final var errors = result.eResource().getErrors();
		Assertions.assertTrue(errors.isEmpty(), "Unexpected errors: %s".formatted(errors.stream().map(Object::toString).collect(
				Collectors.joining(", "))));
	}
}
