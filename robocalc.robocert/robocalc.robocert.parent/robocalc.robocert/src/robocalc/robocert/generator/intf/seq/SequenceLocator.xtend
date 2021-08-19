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
	 * Gets the fully qualified CSP name of a sequence group's target.
	 * 
	 * We assume that we want the closed form of the sequence group.
	 * 
	 * @param it  the sequence group whose target is to be located.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of the target.
	 */	
	def CharSequence getFullTargetCSPName(SequenceGroup it)
}