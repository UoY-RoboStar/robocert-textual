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
package robocalc.robocert.tests.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import robostar.robocert.ExtensionalMessageSet;
import robostar.robocert.MessageSet;
import robostar.robocert.RefMessageSet;
import robostar.robocert.RoboCertFactory;
import robostar.robocert.util.SetFactory;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/**
 * Tests any custom functionality on RefMessageSets, and also tests that the
 * factory resolves it correctly.
 *
 * @author Matt Windsor
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(RoboCertInjectorProvider.class)
public class RefMessageSetImplCustomTest {
	@Inject
	protected RoboCertFactory rf;

	@Inject
	protected SetFactory sf;

	/**
	 * Tests isActive on various forms of reference.
	 */
	@Test
	public void testIsActive() {
		assertFalse(nullRef().isActive());
		assertFalse(nullIndirectRef().isActive());
		assertFalse(refTo(sf.empty()).isActive());
		assertTrue(refTo(single()).isActive());
		assertTrue(refTo(sf.universe()).isActive());
	}

	/**
	 * Tests isUniversal on various forms of reference.
	 */
	@Test
	public void testIsUniversal() {
		assertFalse(nullRef().isUniversal());
		assertFalse(nullIndirectRef().isUniversal());
		assertFalse(refTo(sf.empty()).isUniversal());
		assertFalse(refTo(single()).isUniversal());
		assertTrue(refTo(sf.universe()).isUniversal());
	}

	private RefMessageSet nullRef() {
		return rf.createRefMessageSet();
	}

	private RefMessageSet nullIndirectRef() {
		final var ref = nullRef();
		ref.setSet(rf.createNamedMessageSet());
		return ref;
	}

	private RefMessageSet refTo(final MessageSet ms) {
		final var ref = nullIndirectRef();
		ref.getSet().setSet(ms);
		return ref;
	}

	private ExtensionalMessageSet single() {
		final var set = rf.createExtensionalMessageSet();
		set.getMessages().add(rf.createMessage());
		return set;
	}
}
