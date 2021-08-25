package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.generator.intf.seq.SeqGroupField
import robocalc.robocert.generator.tockcsp.ll.TickTockContextGenerator

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
			case SEQUENCE_MODULE: "Seqs"
			case TARGET: "Target"
			case TICK_TOCK_CONTEXT: TickTockContextGenerator::CONTEXT_MODULE
		}
	}
}