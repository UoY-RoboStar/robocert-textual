package robocalc.robocert.generator.utils

import robocalc.robocert.model.robocert.RAPackage
import java.util.Iterator
import circus.robocalc.robochart.NamedElement
import robocalc.robocert.model.robocert.RCModuleTarget
import com.google.common.collect.Iterators
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.SequenceGroup
import java.util.Collections

/**
 * Extensions for dealing with assertion packages.
 */
class RAPackageExtensions {
	/**
	 * Gets a list of RoboChart named elements referenced by this assertions
	 * package.
	 * 
	 * @param it  the assertions package.
	 *
	 * @returns an iterator of named elements.
	 */
	def Iterator<NamedElement> getReferencedElements(RAPackage it) {
		sequenceGroups.iterator.flatMap[referencedElements]
		// TODO(@MattWindsor91): do we need anything else?
	}

	private def Iterator<NamedElement> getReferencedElements(SequenceGroup it) {
		target.targetElement
	}

	private def dispatch Iterator<NamedElement> getTargetElement(RCModuleTarget it) {
		Iterators.singletonIterator(module)
	}

	private def dispatch Iterator<NamedElement> getTargetElement(Target it) {
		Collections.emptyIterator
	}
}