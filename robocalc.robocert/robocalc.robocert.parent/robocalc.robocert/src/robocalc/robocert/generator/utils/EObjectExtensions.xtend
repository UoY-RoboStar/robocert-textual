/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import org.eclipse.xtext.naming.IQualifiedNameProvider
import com.google.inject.Inject
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.EcoreUtil2
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * General extensions for EObjects.
 */
class EObjectExtensions {
	@Inject extension IQualifiedNameProvider
	
	/**
	 * Gets the identifier of an EObject.
	 * 
	 * @param it  the object.
	 * @return an underscore-delimited encoding of this object's fully qualified name.
	 */
	def getId(EObject it) {
		// This was `id` in GeneratorUtils originally.
		fullyQualifiedName.toString("_")
	}
	
	/**
	 * If the given EObject is a sequence group, retrieves its target.
	 * 
	 * @param it  the object.
	 * 
	 * @return  the underlying target (can be null).
	 */
	def Target getTargetOfParentGroup(EObject it) {
		EcoreUtil2.getContainerOfType(it, SequenceGroup)?.target
	}
}