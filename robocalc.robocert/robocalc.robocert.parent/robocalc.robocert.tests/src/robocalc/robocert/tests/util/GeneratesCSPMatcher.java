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

import com.google.common.base.Strings;
import java.util.function.Function;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Matcher that checks the generator output for a particular object produces CSP-M that normalises
 * to a given form.
 *
 * @author Matt Windsor
 */
public class GeneratesCSPMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
  private final String expected;
  private final Function<T, CharSequence> generator;

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

  /**
   * Shorthand for constructing a generates-CSP matcher.
   *
   * @param <T> type of EObject being generated.
   * @param expected the expected value modulo light normalisation.
   * @param generator the generator to test.
   * @return the matcher.
   */
  public static <T> GeneratesCSPMatcher<T> generatesCSP(
      String expected, Function<T, CharSequence> generator) {
    return new GeneratesCSPMatcher<>(expected, generator);
  }

  private static String tidy(String it) {
    // Compress whitespace
    var tidied = it.strip().replaceAll("\\s+", " ");
    // Remove inner whitespace in delimiters
    tidied = tidied.replaceAll("\\( +", "(").replaceAll(" +\\)", ")");
    tidied = tidied.replaceAll("\\{ +", "{").replaceAll(" +}", "}");
    tidied = tidied.replaceAll("\\{\\| +", "{|").replaceAll(" +\\|}", "|}");
    return tidied;
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
    if (result.equals(expected)) return true;

    final var prefix = Strings.commonPrefix(result, expected);
    var suffix = Strings.commonSuffix(result, expected);
    final var lpoint = prefix.length();
    var rpoint = result.length() - suffix.length();
    // It might be that the difference is that something got inserted in 'expected' that is absent
    // in 'result', and the removal causes the common prefix and suffix to overlap.  Here, we
    // resolve the overlap by trimming the suffix.
    if (rpoint <= lpoint) {
      suffix = suffix.substring(lpoint - rpoint);
      rpoint = lpoint;
    }
    final var different = result.substring(lpoint, rpoint);

    mismatchDescription.appendText("got CSP-M («» = difference): ");
    mismatchDescription.appendText(prefix);
    mismatchDescription.appendText("«");
    mismatchDescription.appendText(different);
    mismatchDescription.appendText("»");
    mismatchDescription.appendText(suffix);

    return false;
  }
}
