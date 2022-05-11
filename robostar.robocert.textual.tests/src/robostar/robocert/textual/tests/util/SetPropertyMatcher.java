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
package robostar.robocert.textual.tests.util;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import robostar.robocert.MessageSet;

public class SetPropertyMatcher extends TypeSafeDiagnosingMatcher<MessageSet> {
  private Boolean isUniversal;
  private Boolean isActive;

  /**
   * @return a matcher that requires the set to be universal.
   */
  public static SetPropertyMatcher universal() {
    return new SetPropertyMatcher().andUniversal();
  }

  /**
   * @return a matcher that requires the set to be not universal.
   */
  public static SetPropertyMatcher notUniversal() {
    return new SetPropertyMatcher().andNotUniversal();
  }

  /**
   * @return a matcher that requires the set to be active.
   */
  public static SetPropertyMatcher active() {
    return new SetPropertyMatcher().andActive();
  }

  /**
   * @return a matcher that requires the set to be inactive.
   */
  public static SetPropertyMatcher inactive() {
    return new SetPropertyMatcher().andInactive();
  }

  /**
   * Sets this matcher to require a universal set.
   *
   * @return this object.
   */
  public SetPropertyMatcher andUniversal() {
    isUniversal = Boolean.TRUE;

    return this;
  }

  /**
   * Sets this matcher to require a non-universal set.
   *
   * @return this object.
   */
  public SetPropertyMatcher andNotUniversal() {
    isUniversal = Boolean.FALSE;

    return this;
  }

  /**
   * Sets this matcher to require an active set.
   *
   * @return this object.
   */
  public SetPropertyMatcher andActive() {
    isActive = Boolean.TRUE;
    return this;
  }

  /**
   * Sets this matcher to require an inactive set.
   *
   * @return this object.
   */
  public SetPropertyMatcher andInactive() {
    isActive = Boolean.FALSE;
    return this;
  }

  @Override
  protected boolean matchesSafely(MessageSet messageSet, Description description) {
    final var u = matchTri("universal", isUniversal, messageSet.isUniversal(), description);
    final var a = matchTri("active", isActive, messageSet.isActive(), description);

    return u && a;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("set that is ");
    describeTri("universal", isUniversal, description);
    description.appendText(" and ");
    describeTri("active", isActive, description);
  }

  private boolean matchTri(String qualifier, Boolean tri, boolean actual, Description description) {
    // this means we don't have a particular requirement here.
    if (tri == null)
      return true;

    final var matched = tri == actual;
    if (!matched) {
      description.appendText("set was ");
      describeTri(qualifier, actual, description);
    }

    return matched;
  }

  private void describeTri(String qualifier, Boolean tri, Description description) {
    if (tri == null)
      description.appendText("maybe ");
    else if (!tri)
      description.appendText("not ");
    description.appendText(qualifier);
  }
}
