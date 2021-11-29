package robocalc.robocert.generator.utils;

import java.util.stream.Stream;

import circus.robocalc.robochart.NamedElement;
import robocalc.robocert.model.robocert.CertPackage;
import robocalc.robocert.model.robocert.SpecGroup;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Extensions for dealing with assertion packages.
 *
 * @author Matt Windsor
 */
public class CertPackageExtensions {
	/**
	 * Gets a stream of RoboChart named elements referenced by targets inside a
	 * package.
	 *
	 * @param p the package.
	 *
	 * @returns a stream of target-referenced named elements.
	 */
	public Stream<NamedElement> getReferencedElements(CertPackage p) {
		return specGroups(p).map(x -> x.getTarget().getElement());
		// TODO(@MattWindsor91): do we need anything else?
	}

	private Stream<SpecGroup> specGroups(CertPackage p) {
		return StreamHelpers.filter(p.getGroups().parallelStream(), SpecGroup.class);
	}
}