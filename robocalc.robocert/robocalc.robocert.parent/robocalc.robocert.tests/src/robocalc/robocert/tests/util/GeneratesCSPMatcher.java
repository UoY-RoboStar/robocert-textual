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

import java.util.function.Function;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.google.common.base.Strings;

/**
 * Matcher that checks the generator output for a particular object produces
 * CSP-M that normalises to a given form.
 * 
 * @author Matt Windsor
 */
public class GeneratesCSPMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
	private String expected;
	private Function<T, CharSequence> generator;
	
	/**
	 * @param <T> type of EObject being generated.
	 * @param expected the expected value modulo light normalisation.
	 * @param generator the generator to test.
	 * @return the matcher.
	 */
	public static <T> GeneratesCSPMatcher<T> generatesCSP(String expected, Function<T, CharSequence> generator) {
		return new GeneratesCSPMatcher<T>(expected, generator);
	}
	
	/**
	 * Constructs a generates-CSP matcher.
	 *
	 * @param expected the expected value modulo light normalisation.
	 * @param generator the generator to test.
	 */
	public GeneratesCSPMatcher(String expected, Function<T, CharSequence> generator) {
		super();
		this.expected = tidy(expected);		
		this.generator = generator;
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("CSP-M equivalent to: ");
		description.appendText(expected);
	}

	@Override
	protected boolean matchesSafely(T item, Description mismatchDescription) {	
		var result = generator.apply(item).toString();
		if (Strings.isNullOrEmpty(result) && !Strings.isNullOrEmpty(expected)) {
			mismatchDescription.appendText("empty generation");
			return false;
		}
		result = tidy(result);
		if (result.equals(expected))
			return true;
		
		var prefix = Strings.commonPrefix(result, expected);
		var suffix = Strings.commonSuffix(result, expected);
		var different = result.substring(prefix.length(), result.length() - suffix.length());
		
		mismatchDescription.appendText("got CSP-M ([[ ]] = difference): ");
		mismatchDescription.appendText(prefix);
		mismatchDescription.appendText("[[");
		mismatchDescription.appendText(different);
		mismatchDescription.appendText("]]");
		mismatchDescription.appendText(suffix);
		
		return false;
	}
	
	private static String tidy(String it) {
		// Compress whitespace
		var tidied = it.strip().replaceAll("\\s+", " ");
		// Remove inner whitespace in delimiters
		tidied = tidied.replaceAll("\\( +", "(").replaceAll(" +\\)", ")");
		tidied = tidied.replaceAll("\\{ +", "{").replaceAll(" +\\}", "}");
		tidied = tidied.replaceAll("\\{\\| +", "{|").replaceAll(" +\\|\\}", "|}");
		return tidied;
	}
}
