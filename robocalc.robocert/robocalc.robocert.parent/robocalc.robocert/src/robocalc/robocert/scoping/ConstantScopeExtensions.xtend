package robocalc.robocert.scoping

import robocalc.robocert.model.robocert.ConstAssignment
import robocalc.robocert.generator.utils.TargetExtensions
import com.google.inject.Inject
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.SequenceProperty
import org.eclipse.xtext.scoping.Scopes
import java.util.Iterator
import circus.robocalc.robochart.Variable
import static extension org.eclipse.xtext.EcoreUtil2.getContainerOfType
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.scoping.IScope

/**
 * Scoping logic for constants.
 */
class ConstantScopeExtensions {
	@Inject extension TargetExtensions
	@Inject IQualifiedNameProvider qnp

	/**
	 * Tries to resolve the constant scope for a constant assignment.
	 * 
	 * @param it  the constant assignment for which we are resolving the scope.
	 * 
	 * @return the scope (may be null).
	 */
	def constAssignmentScope(ConstAssignment it) {
		sequenceConstAssignmentScope ?: assertionConstAssignmentScope
	}

	/**
	 * Tries to get the scope of a constant assignment by walking back to a
	 * target actor, then retrieving its parametrisation.
	 */
	private def sequenceConstAssignmentScope(ConstAssignment it) {
		// TODO(@MattWindsor91): this should be part of the metamodel, somehow.
		getContainerOfType(Target)?.targetScope
	}

	/**
	 * Gets the target of a constant assignment by walking back to an
	 * assertion, then retrieving its sequence's target's uninstantiated
	 * constants.
	 */
	private def assertionConstAssignmentScope(ConstAssignment it) {
		// TODO(@MattWindsor91): this should be part of the metamodel, somehow.
		getContainerOfType(SequenceProperty)?.sequence?.target?.uninstantiatedTargetScope
	}

	/**
	 * Produces a scope containing all of the constants defined on a target.
	 * 
	 * @param it  the target in question.
	 * 
	 * @return  the target's constants as a scope.
	 */
	private def targetScope(Target it) {
		scopeFor(parameterisation)
	}

	/**
	 * Produces a scope containing any uninstantiated constants defined on a target.
	 * 
	 * @param it  the target in question.
	 * 
	 * @return  the target's constants as a scope.
	 */
	private def uninstantiatedTargetScope(Target it) {
		scopeFor(uninstantiatedConstants(group?.instantiation))
	}

	/**
	 * Calculates a scope given an iterator of constants.
	 * 
	 * The calculated scope brings every constant into scope on its qualified
	 * name, and then (for now) overlays the unqualified names also.  This
	 * behaviour may change later on, as it introduces ambiguities that may be
	 * resolved in surprising manners.
	 * 
	 * @param it  the constant iterator.
	 * 
	 * @return  the iterator as a scope as described above.
	 */
	private def scopeFor(Iterator<Variable> it) {
		// Need to make sure we expand the iterator only once.
		// Can't convert to a set as that compresses variables with similar names.
		val vars = toList;

		// TODO(@MattWindsor91): this shouldn't bring ambiguous names into
		// scope, or there should at least be a validation issue for it.
		Scopes.scopeFor(vars, vars.fullyQualifiedScope)
	}

	/**
	 * Calculates a scope bringing every given constant into scope on its
	 * fully qualified name.
	 * 
	 * @param it  the constant iterator.
	 * 
	 * @return  the iterator as a scope as described above.
	 */
	private def fullyQualifiedScope(Iterable<Variable> it) {
		Scopes.scopeFor(it, qnp, IScope.NULLSCOPE)
	}
}
