package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.generator.intf.seq.SequenceLocator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.intf.seq.SeqGroupField
import com.google.inject.Inject
import robocalc.robocert.generator.intf.seq.SeqGroupParametricField

/**
 * Implementation of SequenceLocator.
 */
class SequenceLocatorImpl implements SequenceLocator {
	@Inject extension SeqGroupFieldGenerator
	
	override getFullCSPName(Sequence it) '''«group.getFullCSPName(SeqGroupParametricField::SEQUENCE_MODULE)»::«name»'''
	
	override getFullCSPName(SequenceGroup it, SeqGroupField field) '''«name»::«field.generate»'''
		
	override getFullCSPName(SequenceGroup it, SeqGroupParametricField field) '''«prefix»::«field.generate»'''
	
	private def prefix(SequenceGroup it) {
		getFullCSPName(SeqGroupField::PARAMETRIC_CLOSED)
	}

}