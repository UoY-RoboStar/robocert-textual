package robocalc.robocert.generator

import robocalc.robocert.model.robocert.SequenceStep
import java.util.List

/**
 * A pair of sequence step and index.
 */
class IndexedSequenceStep {
	new(SequenceStep step, int index) {
		this.step = step;
		this.index = index;
	}
	
	new() {
		this(null, 0)
	}
	
	SequenceStep step;
	int index;
	
	def SequenceStep getStep() {
		this.step
	}
	
	def int getIndex() {
		this.index
	}
	
	/**
	 * Produces an array of indexed sequence steps given a list of sequence
	 * steps.
	 * 
	 * @param steps  the steps to index.
	 * @return an array of indexed steps.
	 */
	def static IndexedSequenceStep[] enumerate(List<SequenceStep> steps) {
		var IndexedSequenceStep[] pairs = newArrayOfSize(steps.length);
		var i = 0;
		
		for (step : steps) {
			pairs.set(i, new IndexedSequenceStep(step, i));
			i++;
		}
		
		pairs
	}
}