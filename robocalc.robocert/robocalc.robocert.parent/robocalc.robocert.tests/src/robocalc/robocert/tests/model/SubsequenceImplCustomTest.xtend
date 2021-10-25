package robocalc.robocert.tests.model

import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import org.eclipse.xtext.testing.InjectWith
import robocalc.robocert.tests.RoboCertInjectorProvider
import robocalc.robocert.model.robocert.RoboCertFactory
import com.google.inject.Inject
import org.junit.jupiter.api.Test
import static extension org.junit.Assert.*

/**
 * Tests any custom functionality on Subsequences, and also tests that the factory
 * resolves it correctly.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertInjectorProvider) 
class SubsequenceImplCustomTest {
	@Inject RoboCertFactory rf
	
	/**
	 * Tests that we return null if there is no parent sequence.
	 */
	@Test
	def testSequence_Null() {
		rf.createSubsequence.sequence.assertNull
	}
	
	
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
				steps.add(rf.createLoopStep => [
					body = sseq
				])
			]
		]
		
		seq.assertEquals(sseq.sequence)
	}	
}