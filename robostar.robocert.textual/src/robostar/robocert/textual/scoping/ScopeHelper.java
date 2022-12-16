/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.scoping;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import com.google.inject.Inject;

import robostar.robocert.SpecificationGroup;
import robostar.robocert.util.resolve.ResolveHelper;

/**
 * Contains various helper methods for scoping.
 *
 * @author Matt Windsor
 */
public record ScopeHelper(IQualifiedNameProvider qnp) {
	@Inject
	public ScopeHelper {
		Objects.requireNonNull(qnp);
	}

	/**
	 * Calculates a scope with both unqualified and qualified forms.
	 *
	 * <p>
	 * The calculated scope brings every constant into scope on its qualified name,
	 * and then (for now) overlays the unqualified names also. This behaviour may
	 * change later on, as it introduces ambiguities that may be resolved in
	 * surprising manners.
	 *
	 * @param it the iterable of items to bring into scope.
	 *
	 * @return the iterator as a scope as described above.
	 */
	public IScope unifiedScope(Iterable<? extends EObject> it) {
		// TODO(@MattWindsor91): this shouldn't bring ambiguous names into
		// scope, or there should at least be a validation issue for it.
		return Scopes.scopeFor(it, fullyQualifiedScope(it));
	}

	/**
	 * Calculates a scope bringing every given constant into scope on its fully
	 * qualified name.
	 *
	 * @param it the iterable of items to bring into scope.
	 *
	 * @return the iterator as a scope as described above.
	 */
	public IScope fullyQualifiedScope(Iterable<? extends EObject> it) {
		return Scopes.scopeFor(it, qnp, IScope.NULLSCOPE);
	}
	
	// TODO(@MattWindsor91): I think these can be pushed down into robocert-metamodel.
	
	/**
	 * Wrapper over getting the specification group of an item.
	 * 
	 * @param ele   element to inspect.
	 * @return the specification group containing the given type as an optional.
	 */
	public Optional<SpecificationGroup> specificationGroupOf(EObject ele) {
		return ResolveHelper.containerOfType(ele, SpecificationGroup.class);
	}
}
