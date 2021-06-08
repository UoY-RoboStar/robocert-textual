/* NOTE: a lot of this code is adapted from GeneratorUtils in the upstream
 * RoboChart CSP generator, and as such contributors include:
 * 
 * - Alvaro Miyazawa
 * - Pedro Ribeiro
 */
package robocalc.robocert.generator.utils

import circus.robocalc.robochart.RCPackage
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.EObject
import circus.robocalc.robochart.BasicPackage

/**
 * Extension methods for dealing with RoboChart packages.
 */
class RCPackageExtensions {
	/**
	 * Gets the RoboChart packages in an EMF resource.
	 * 
	 * @param parent  the top-level resource.
	 * 
	 * @return  the packages available on this resource.
	 */
	def Iterable<RCPackage> getPackages(Resource parent) {
		// TODO(@MattWindsor91): understand why we filter on file extension.
		parent.resourceSet.resources.filter[it !== parent && URI.fileExtension == "rct"].flatMap[package]
	}
	
	/**
	 * Gets an iterable over the RoboChart package in this object's resource,
	 * if any.
	 * 
	 * @parent it  the object.
	 * 
	 * @return  an iterable over the object's resource's package.
	 */
	def Iterable<RCPackage> getPackage(EObject it) {
		// TODO(@MattWindsor91): understand exactly what this is doing.
		eResource.package
	}

	/**
	 * Gets an iterable over the RoboChart package in this resource,
	 * if any.
	 * 
	 * This is an iterable because the resource may be empty or not have a
	 * RoboChart package as its first content.
	 * 
	 * @parent it  the resource.
	 * 
	 * @return  an iterable over the resource's package.
	 */
	def Iterable<RCPackage> getPackage(Resource it) {
		contents.take(1).filter(RCPackage)
	}
	
	/**
	 * Gets an iterable over the basic package in this resource,
	 * if any.
	 * 
	 * This is an iterable because the resource may be empty or not have a
	 * basic package as its first content.
	 * 
	 * @parent it  the resource.
	 * 
	 * @return  an iterable over the resource's package.
	 */
	def Iterable<BasicPackage> getBasicPackage(Resource it) {
		contents.take(1).filter(BasicPackage)
	}
}
