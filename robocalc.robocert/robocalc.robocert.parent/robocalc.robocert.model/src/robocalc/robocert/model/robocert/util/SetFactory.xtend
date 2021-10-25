package robocalc.robocert.model.robocert.util

import robocalc.robocert.model.robocert.RoboCertFactory
import com.google.inject.Inject
import robocalc.robocert.model.robocert.MessageSet
import robocalc.robocert.model.robocert.BinarySetOperator
import robocalc.robocert.model.robocert.MessageSpec

/**
 * Contains utility methods for constructing sets.
 */
class SetFactory {
	@Inject RoboCertFactory rf
	
	/**
	 * @param elements  the contents to put into the set.
	 * @return an extensional set with the given contents.
	 */
	def extensional(Iterable<MessageSpec> elements) {
		rf.createExtensionalMessageSet=>[messages.addAll(elements)]
	}
	
	/**
	 * @param element  the content to put into the set.
	 * @return a singleton extensional set with the given element.
	 */
	def singleton(MessageSpec element) {
		rf.createExtensionalMessageSet=>[messages.add(element)]
	}
	
	/**
	 * @return a universe set.
	 */
	def universe() {
		rf.createUniverseMessageSet
	}
	
	/**
	 * @return an empty set.
	 */
	def empty() {
		rf.createExtensionalMessageSet
	}

	/**
	 * @param l  the left-hand side set.
	 * @param r  the right-hand side set.
	 * @return a set representing the union of the two sets.
	 */
	def union(MessageSet l, MessageSet r) {
		binary(l, BinarySetOperator::UNION, r)
	}
	
	/**
	 * @param l  the left-hand side set.
	 * @param r  the right-hand side set.
	 * @return a set representing the intersection of the two sets.
	 */	
	def inter(MessageSet l, MessageSet r) {
		binary(l, BinarySetOperator::INTERSECTION, r)
	}
	/**
	 * @param l  the left-hand side set.
	 * @param r  the right-hand side set.
	 * @return a set representing the difference of the LHS by the RHS.
	 */	
	def diff(MessageSet l, MessageSet r) {
		binary(l, BinarySetOperator::DIFFERENCE, r)
	}
	
	/**
	 * @param l  the left-hand side set.
	 * @param o  the operator.
	 * @param r  the right-hand side set.
	 * @return a set representing the application of the operator to the two
	 *         sets.
	 */	
	def binary(MessageSet l, BinarySetOperator o, MessageSet r) {
		rf.createBinaryMessageSet=>[
			lhs = l
			operator = o
			rhs = r
		]
	}
}