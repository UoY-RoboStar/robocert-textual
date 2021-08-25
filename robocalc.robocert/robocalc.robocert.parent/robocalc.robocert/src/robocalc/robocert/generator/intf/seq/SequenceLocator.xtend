package robocalc.robocert.generator.intf.seq

import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * Abstracts over the act of getting CSP names for sequence components.
 * 
 * This mainly exists to break a dependency cycle between the CSP generators
 * and sequence generators.
 */
interface SequenceLocator {
	/**
	 * Gets the fully qualified CSP name of a sequence, including its group.
	 * 
	 * We assume that we want the closed form of the sequence group.
	 * 
	 * @param it  the sequence to locate.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of the sequence.
	 */
	def CharSequence getFullCSPName(Sequence it)

	/**
	 * Gets the fully qualified CSP name of a field on a sequence group.
	 * 
	 * @param it     the sequence group whose field is to be located.
	 * @param field  the field in question.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of a field.
	 */	
	def CharSequence getFullCSPName(SequenceGroup it, SeqGroupField field)
	
	/**
	 * Gets the fully qualified CSP name of a field on a sequence group's
	 * parametric part.
	 * 
	 * We assume that we want the closed form of the parametric part.
	 * 
	 * @param it     the sequence group whose field is to be located.
	 * @param field  the field in question.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of a field.
	 */	
	def CharSequence getFullCSPName(SequenceGroup it, SeqGroupParametricField field)	
}