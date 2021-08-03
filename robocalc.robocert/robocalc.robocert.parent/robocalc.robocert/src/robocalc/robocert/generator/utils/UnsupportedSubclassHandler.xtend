package robocalc.robocert.generator.utils

import org.eclipse.emf.ecore.EObject

/**
 * Used to signify in the generator that we've reached a subclass of some sort
 * of semantic element that isn't handled yet.
 */
class UnsupportedSubclassHandler {
	/**
	 * Expands into a fallback for an object of an unsupported subclass.
	 * 
	 * @param it        object that is not supported by this generator path.
	 * @param type      string representation of the semantic type of the item.
	 * @param fallback  CSP-M that is standing in for the subclass.
	 * 
	 * @return  CSP-M fallback for the unsupported object.
	 */
	def CharSequence unsupported(EObject it, CharSequence type, CharSequence fallback)
		'''{- fallback for unsupported «type» («it»)-}
	«fallback»
{- end fallback -}'''
}