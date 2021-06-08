/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.Type
import circus.robocalc.robochart.TypeRef
import circus.robocalc.robochart.SetType
import circus.robocalc.robochart.SeqType
import circus.robocalc.robochart.PrimitiveType
import circus.robocalc.robochart.Enumeration
import circus.robocalc.robochart.Literal
import com.google.inject.Inject
import circus.robocalc.robochart.RecordType
import circus.robocalc.robochart.ProductType

/**
 * Extension methods for working with RoboChart's type system.
 */
class TypeExtensions {
	@Inject extension EObjectExtensions

	/**
	 * Gets the default value of a type reference.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(TypeRef it) {
		ref.defaultValue
	}

	/**
	 * Gets the default value of a primitive type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch defaultValue(PrimitiveType it) {
		switch name {
			case "boolean":
				"true"
			case "string": '''""'''
			default:
				"0"
		}
	}

	/**
	 * Gets the default value of an enumeration.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(Enumeration it) {
		literals.get(0).defaultValue
	}

	/**
	 * Gets the default value of a product type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(ProductType it) '''(«FOR i : types SEPARATOR ','»«i.defaultValue»«ENDFOR»)'''

	/**
	 * Gets the default value of a record type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(
		RecordType it) '''(«FOR f : fields SEPARATOR ','»«f.type.defaultValue»«ENDFOR»)'''

	/**
	 * Gets the default value of a sequence type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(SeqType it) '''<>'''

	/**
	 * Gets the default value of a set type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(SetType it) '''{}'''

	/**
	 * Gets the default value of a literal type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(Literal it)
		'''«id»«FOR x : types BEFORE '.' SEPARATOR '.'»x.defaultValue«ENDFOR»'''

	/**
	 * Gets the default value of an unknown type type.
	 * 
	 * @param it  the type to consider.
	 * 
	 * @return the default value of the type.
	 */
	def dispatch CharSequence defaultValue(Type t) '''{- UNKNOWN TYPE: «t» -}0'''
}
