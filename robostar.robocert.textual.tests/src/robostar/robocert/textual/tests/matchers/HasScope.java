/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.matchers;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.scoping.IScope;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 * Hamcrest matcher that checks to make sure that the given items, and only the given items, are in
 * scope.
 *
 * @author Matt Windsor
 */
public class HasScope extends TypeSafeDiagnosingMatcher<IScope> {
  private final List<EObject> expected;

  /**
   * Constructs a matcher with the given expected objects.
   *
   * @param expected the objects to test against.
   */
  public HasScope(EObject... expected) {
    this.expected = List.of(expected);
  }

  @Override
  protected boolean matchesSafely(IScope scope, Description description) {
    // `expected` is unmodifiable, plus we want this function to be re-entrant.
    final var want = new ArrayList<>(expected);

    for (var x : scope.getAllElements()) {
      final var wanted = want.remove(x.getEObjectOrProxy());
      if (!wanted) {
        description.appendText("unexpected item ");
        description.appendValue(x);
        return false;
      }
    }

    if (!want.isEmpty()) {
      description.appendText("didn't find ");
      description.appendValue(want.get(0));
      return false;
    }

    return true;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("has scope containing the items:");
    for (var x : expected) {
      description.appendText(" ");
      description.appendValue(x);
    }
  }
}
