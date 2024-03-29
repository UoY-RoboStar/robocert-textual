/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.scoping;

import static robostar.robocert.RoboCertPackage.Literals.CONST_ASSIGNMENT__CONSTANTS;
import static robostar.robocert.RoboCertPackage.Literals.EVENT_TOPIC__EFROM;
import static robostar.robocert.RoboCertPackage.Literals.EVENT_TOPIC__ETO;
import static robostar.robocert.RoboCertPackage.Literals.OPERATION_TOPIC__OPERATION;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;

import com.google.inject.Inject;

import circus.robocalc.robochart.EnumExp;
import circus.robocalc.robochart.RefExp;
import circus.robocalc.robochart.RoboChartPackage.Literals;
import circus.robocalc.robochart.TypeRef;
import circus.robocalc.robochart.textual.scoping.RoboChartScopeProvider;
import robostar.robocert.ConstAssignment;
import robostar.robocert.EventTopic;
import robostar.robocert.OperationTopic;

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
public class RoboCertScopeProvider extends AbstractRoboCertScopeProvider {
	@Inject private VariableScopeProvider vsp;
	@Inject private TopicScopeProvider tsp;
	@Inject private EnumScopeProvider esp;	
	@Inject private RoboChartScopeProvider rchart;

	
	@Override
	public IScope getScope(EObject context, EReference reference) {
		final var scope = tryGetScope(context, reference);
		return scope == null ? super.getScope(context, reference) : scope;
	}
	
	/**
	 * Tries to get a custom scope for the given context and reference.
	 *
	 * @param context   context of the feature being resolved.
	 * @param reference reference to the feature being resolved.
	 * 
	 * @return the custom scope (may be null, in which case we delegate to the
	 *         parent scoping rules).
	 */
	private IScope tryGetScope(EObject context, EReference reference) {
		if (context instanceof EventTopic e && isEventReference(reference))
			return tsp.getEventScope(e, reference == EVENT_TOPIC__EFROM);
		if (context instanceof OperationTopic o && reference == OPERATION_TOPIC__OPERATION)
			return tsp.getOperationScope(o);
		if (context instanceof ConstAssignment k && reference == CONST_ASSIGNMENT__CONSTANTS)
			return vsp.constAssignmentScope(k);
		if (context instanceof RefExp x && reference == Literals.REF_EXP__REF)
			return vsp.exprScope(x);
		if (context instanceof EnumExp x)
			return esp.exprScope(x, reference);
		//if (context instanceof EnumExp x && reference == Literals.ENUM_EXP__TYPE)
		//	return enumExpType(x);
		if (context instanceof TypeRef x && reference == Literals.TYPE_REF__REF)
			return rchart.getScope(x, reference);

		// Fallback to normal scope resolution.
		return null;
	}
	
	/*
	private IScope enumExpType(EnumExp x) {

		// TODO
		
	}
	*/

	private boolean isEventReference(EReference reference) {
		return reference == EVENT_TOPIC__EFROM || reference == EVENT_TOPIC__ETO;
	}
}
