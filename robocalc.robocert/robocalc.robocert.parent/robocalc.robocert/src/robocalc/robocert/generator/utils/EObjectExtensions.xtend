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
}