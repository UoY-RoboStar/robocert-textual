/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.scoping

import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import static robocalc.robocert.model.robocert.RoboCertPackage.Literals.*
import com.google.inject.Inject
import robocalc.robocert.model.robocert.ConstAssignment
import robocalc.robocert.model.robocert.OperationTopic
import robocalc.robocert.model.robocert.EventTopic
import robocalc.robocert.generator.utils.EObjectExtensions
import robocalc.robocert.model.robocert.ConstExpr

/**
 * This class contains custom scoping description.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#scoping
 * on how and when to use it.
 */
class RoboCertScopeProvider extends AbstractRoboCertScopeProvider {
	@Inject extension EObjectExtensions
	@Inject extension ConstantScopeExtensions
	@Inject extension TopicScopeExtensions

	
	override getScope(EObject context, EReference reference) {
		getScopeInner(context, reference) ?: super.getScope(context, reference)
	}
	
	/**
	 * Special scoping for events in an event topic.
	 * 
	 * The events in scope here are those that the 'from' of the message can
	 * send to the 'to' of the message.
	 * 
	 * @param context    the scoping context.
	 * @param reference  the reference.
	 * 
	 * @return  the provided scope (can be null).
	 */
	private def dispatch getScopeInner(EventTopic context, EReference reference) {
		if (reference == EVENT_TOPIC__EVENT) {
			context.eventScope
		}
	}

	/**
	 * Special scoping for operations in an operation topic.
	 * 
	 * The operations in scope here are those that the 'from' of the message can
	 * call on the 'to' of the message.
	 * 
	 * @param context    the scoping context.
	 * @param reference  the reference.
	 * 
	 * @return  the provided scope (can be null).
	 */
	private def dispatch getScopeInner(OperationTopic context, EReference reference) {
		if (reference == OPERATION_TOPIC__OPERATION) {
			context.operationScope
		}
	}
	
	/**
	 * Special scoping for constants in a constant assignment.
	 * 
	 * The constants in scope here are those available on the relevant target,
	 * either found enclosing this constant assignment or indirectly through an
	 * enclosing assertion (in which case, the only constants in scope are those
	 * not fixed by the target's instantiation).
	 * 
	 * @param context    the scoping context.
	 * @param reference  the reference.
	 * 
	 * @return  the provided scope (can be null).
	 */
	private def dispatch getScopeInner(ConstAssignment context, EReference reference) {
		if (reference == CONST_ASSIGNMENT__CONSTANTS) {
			context.constAssignmentScope
		}
	}
	
	private def dispatch getScopeInner(ConstExpr context, EReference reference) {
		if (reference == CONST_EXPR__CONSTANT) {
			context.constExprScope
		}
	}
	
	/**
	 * Fallback scoping for when we haven't manually overridden anything.
	 * 
	 * @param context    the scoping context.
	 * @param reference  the reference.
	 * 
	 * @return  null.
	 */
	private def dispatch getScopeInner(EObject context, EReference reference) {
	}
	
	private def constExprScope(ConstExpr it) {
		// TODO(@MattWindsor91): move this.
		targetOfParentGroup?.targetScope
	}
}
