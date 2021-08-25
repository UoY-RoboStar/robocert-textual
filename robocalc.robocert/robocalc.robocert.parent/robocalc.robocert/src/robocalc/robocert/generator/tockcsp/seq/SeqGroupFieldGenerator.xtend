package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.generator.intf.seq.SeqGroupField
import robocalc.robocert.generator.tockcsp.ll.TickTockContextGenerator
import robocalc.robocert.generator.intf.seq.SeqGroupParametricField

/**
 * Names sequence group fields.
 *
 * This class mainly exists to ensure any references to such fields pick up the
 * same names.
 */
class SeqGroupFieldGenerator {
	/**
	 * Generates the name for a sequence group field.
	 * 
	 * @param it  the field in question
	 * 
	 * @return  the identifier (relative to the sequence group) by which that
	 * 			field will be known in the CSP-M script.
	 */
	def CharSequence generate(SeqGroupField it) {
		switch it {
			case MESSAGE_SET_MODULE: "MsgSets"
			case PARAMETRIC_CLOSED: "Closed"
			case PARAMETRIC_OPEN: "Open"
			case TICK_TOCK_CONTEXT: TickTockContextGenerator::CONTEXT_MODULE
		}
	}
	
	/**
	 * Generates the name for a sequence group field inside the parametric part
	 * of a group.
	 * 
	 * @param it  the field in question
	 * 
	 * @return  the identifier (relative to the sequence group) by which that
	 * 			field will be known in the CSP-M script.
	 */
	def CharSequence generate(SeqGroupParametricField it) {
		switch it {
			case SEQUENCE_MODULE: "Seqs"
			case TARGET: "Target"
		}
	}
}