package robocalc.robocert.tests.model

import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.InjectWith
import robocalc.robocert.tests.RoboCertInjectorProvider
import robocalc.robocert.model.robocert.RobocertFactory
import com.google.inject.Inject
import org.junit.jupiter.api.Test
import static extension org.junit.Assert.assertEquals

/**
 * Tests any custom functionality on Subsequences, and also tests that the factory
 * resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class SubsequenceImplCustomTest {
	@Inject RobocertFactory rf
	
	/**
	 * Tests that we can find the subsequence of a root subsequence.
	 */
	@Test
	def testSequence_RootSubsequence() {
		val seq = rf.createSequence => [
			body = rf.createSubsequence
		]
		
		seq.assertEquals(seq.body.sequence)
	}
	
	
	/**
	 * Tests that we can find the subsequence of a singly nested loop
	 * subsequence.
	 */
	@Test
	def testSequence_LoopSubsequence() {
		val sseq = rf.createSubsequence
		
		val seq = rf.createSequence => [
			body = rf.createSubsequence => [
				steps.add(rf.createSequenceStep => [
					gap = rf.createSequenceGap
					action = rf.createLoopAction => [
						body = sseq
					]
				])
			]
		]
		
		seq.assertEquals(sseq.sequence)
	}	
}