/********************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robocalc.robocert.tests.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Matcher that tests for EcoreUtil structural equality.
 * 
 * @author Matt Windsor
 */
public class IsStructurallyEqualTo<T extends EObject> extends TypeSafeMatcher<T> {
	private T expected;
	
	/**
	 * Constructs a matcher with the given expected object.
	 *
	 * @param expected the object to test against.
	 */	
	public static <T extends EObject> IsStructurallyEqualTo<T> structurallyEqualTo(T expected) {
		return new IsStructurallyEqualTo<>(expected);
	}
	
	/**
	 * Constructs a matcher with the given expected object.
	 *
	 * @param expected the object to test against.
	 */
	public IsStructurallyEqualTo(T expected) {
		this.expected = expected;
	}
	
    @Override
    protected boolean matchesSafely(T actual) {
        return EcoreUtil2.equals(expected, actual);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("structurally equal to ").appendValue(expected);
    }
}
