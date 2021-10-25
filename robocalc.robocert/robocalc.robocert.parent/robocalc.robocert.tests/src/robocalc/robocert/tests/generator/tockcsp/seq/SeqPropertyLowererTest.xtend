package robocalc.robocert.tests.generator.tockcsp.seq

import org.junit.jupiter.api.^extension.ExtendWith
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.extensions.InjectionExtension
import robocalc.robocert.tests.util.RoboCertCustomInjectorProvider
import org.junit.jupiter.api.Test
import robocalc.robocert.model.robocert.RoboCertFactory
import com.google.inject.Inject
import circus.robocalc.robochart.RoboChartFactory
import robocalc.robocert.model.robocert.CSPModel
import robocalc.robocert.model.robocert.SequencePropertyType
import robocalc.robocert.generator.tockcsp.seq.SeqPropertyLowerer
import static extension org.junit.jupiter.api.Assertions.*
import robocalc.robocert.model.robocert.CSPProcessSource
import robocalc.robocert.model.robocert.CSPRefinementProperty
import robocalc.robocert.model.robocert.CSPRefinementOperator

/**
 * Tests sequence property lowering.
 */
@ExtendWith(InjectionExtension)
@InjectWith(RoboCertCustomInjectorProvider) 
class SeqPropertyLowererTest {
	@Inject RoboCertFactory rf
	@Inject RoboChartFactory rcf
	@Inject extension SeqPropertyLowerer
	
	/**
	 * Tests that lowering a traces holds property produces the expected refinement.
	 */
	@Test
	def void testLower_Traces_Holds() {
		val prop = property(SequencePropertyType::HOLDS, CSPModel::TRACES)	
		assertCSP(prop.lower, prop.sequence?.target, prop.sequence, prop.model)
	}
	
	/**
	 * Tests that lowering a tick-tock is-observed property produces the expected refinement.
	 */
	@Test
	def void testLower_TickTock_IsObserved() {
		val prop = property(SequencePropertyType::IS_OBSERVED, CSPModel::TICK_TOCK)	
		assertCSP(prop.lower, prop.sequence, prop.sequence?.target, prop.model)
	}
	
	/**
	 * Tests that the given CSP property has the given LHS, RHS, and model;
	 * that it is a refinement;
	 * and that nothing has been set to null along the way.
	 * 
	 * @param it  the property to check.
	 * @param l   the expected LHS.
	 * @param r   the expected RHS.
	 * @param m   the expected model.
	 */
	private def assertCSP(CSPRefinementProperty it, CSPProcessSource l, CSPProcessSource r, CSPModel m) {
		assertNotNull		
		
		lhs.assertNotNull
		l.assertEquals(lhs)
		
		rhs.assertNotNull
		r.assertEquals(rhs)
		
		m.assertNotNull
		m.assertEquals(model)
		
		CSPRefinementOperator::REFINES.assertEquals(type)
	}
	
	private def property(SequencePropertyType t, CSPModel m) {
		rf.createSequenceProperty=>[
			sequence = sequence()
			type = t
			model = m
		]
	}
	
	private def sequence() {
		rf.createSequence=>[
			name = "seq"
			group = rf.createSequenceGroup=>[
				world = rf.createWorld
				target = target()
			]
		]
	}
	
	private def target() {
		rf.createRCModuleTarget=>[
			module=rcf.createRCModule=>[
				name="mod"
			]
		]
	}
}