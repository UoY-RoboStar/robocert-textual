package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.generator.intf.seq.SequenceLocator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.intf.seq.SeqGroupField
import com.google.inject.Inject

/**
 * Implementation of SequenceLocator.
 */
class SequenceLocatorImpl implements SequenceLocator {
	@Inject extension SeqGroupFieldGenerator
	
	override getFullCSPName(Sequence it) '''«group.getFullCSPName(SeqGroupField::SEQUENCE_MODULE)»::«name»'''
	
	override getFullCSPName(SequenceGroup it, SeqGroupField field) '''«prefix»::«field.generate»'''
	
	// TODO(@MattWindsor91): reconcile with seqgroupgenerator version
	private def prefix(SequenceGroup it) '''«name»::«SeqGroupGenerator::CLOSED_DEF_MODULE_NAME»'''
}