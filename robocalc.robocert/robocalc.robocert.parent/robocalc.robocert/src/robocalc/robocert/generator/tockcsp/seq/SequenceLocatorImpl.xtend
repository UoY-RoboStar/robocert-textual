package robocalc.robocert.generator.tockcsp.seq

import robocalc.robocert.generator.intf.seq.SequenceLocator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup

/**
 * Implementation of SequenceLocator.
 */
class SequenceLocatorImpl implements SequenceLocator {
	
	override getFullCSPName(Sequence it) '''«group.prefix»::«SeqGroupGenerator::SEQUENCES_MODULE_NAME»::«name»'''
	
	override getFullTargetCSPName(SequenceGroup it) '''«prefix»::«SeqGroupGenerator::TARGET_DEF_NAME»'''
	
	// TODO(@MattWindsor91): reconcile with seqgroupgenerator version
	private def prefix(SequenceGroup it) '''«name»::«SeqGroupGenerator::CLOSED_DEF_MODULE_NAME»'''
}