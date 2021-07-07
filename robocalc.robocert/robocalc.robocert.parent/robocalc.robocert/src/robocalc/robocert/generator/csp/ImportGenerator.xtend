package robocalc.robocert.generator.csp

import circus.robocalc.robochart.BasicPackage
import circus.robocalc.robochart.Import
import circus.robocalc.robochart.NamedElement
import circus.robocalc.robochart.RCPackage
import com.google.inject.Inject
import java.util.LinkedList
import java.util.Set
import org.eclipse.emf.ecore.resource.Resource
import robocalc.robocert.generator.utils.RCPackageExtensions
import robocalc.robocert.model.robocert.RAPackage
import com.google.common.collect.Iterators
import java.util.Iterator
import robocalc.robocert.generator.utils.FilenameExtensions
import robocalc.robocert.generator.utils.RAPackageExtensions

/**
 * A generator that expands out imports for a top-level resource.
 */
class ImportGenerator {
	// TODO(@MattWindsor91): we assume there is an instantiations.csp file
	// at the moment, and, as such, a) import it; and b) import every package
	// rather than just the anonymous ones.  This should be fixed in line with
	// upstream, eventually, I think?
	@Inject extension RAPackageExtensions
	@Inject extension RCPackageExtensions
	@Inject extension FilenameExtensions

	/**
	 * Generates imports.
	 * 
	 * @return  the generated imports.
	 */
	def CharSequence generateImports(Resource resource) '''
		«FOR p : resource.imports»
			include "«p»"
		«ENDFOR»
	'''

	// Pulled out of GeneratorUtils
	private def getImports(Resource it) {
		// Trying to make a set out of CharSequences doesn't seem to work
		// properly, hence the conversion.
		Iterators.concat(
			standardImports,
			resourceImports,
			packageImports
		).map[toString].toSet
	}

	/**
	 * Gets the standard RoboChart definitions imports.
	 * 
	 * @return  an iterator of import filenames.
	 */
	private def standardImports() {
		// TODO(@MattWindsor91): remove instantiations.csp eventually?
		Iterators.forArray(#["defs/timed_definitions.csp", "defs/robochart_defs.csp", "defs/core_defs.csp", "defs/robocert_defs.csp", "instantiations.csp"])
	}

	/**
	 * Gets 'defs' imports for any package reachable through this resource's set.
	 * 
	 * @param it  the resource to query.
	 * 
	 * @return  an iterator of import filenames.
	 */
	private def getResourceImports(Resource it) {
		// TODO(@MattWindsor91): this may sometimes need to be filtered to
		// anonymous only.
		packages.iterator.map[CSPDefsFileName]
	}

	/**
	 * Scrapes the resource's package for imports.
	 * 
	 * The exact type of imports retrieved in this way depends on the
	 * type of package.
	 * 
	 * @param it  the resource to query.
	 * 
	 * @return  an iterator of input filenames.
	 */
	private def Iterator<CharSequence> getPackageImports(Resource it) {
		basicPackage.iterator.flatMap [
			Iterators.concat(
				namedImports,
				packageImportsInner
			)
		]
	}

	private def dispatch getPackageImportsInner(RAPackage it) {
		referencedElements.flatMap[elementImports]
	}

	private def dispatch getPackageImportsInner(RCPackage it) {
		Iterators.singletonIterator(CSPMainFileName)
	}

	private def Iterator<CharSequence> getElementImports(NamedElement element) {
		element.package.iterator.flatMap [
			Iterators.concat(
				Iterators.singletonIterator(CSPDefsFileName),
				Iterators.singletonIterator(element.CSPTopModuleFileName),
				namedImports
			)
		]
	}

	private def getNamedImports(BasicPackage it) {
		allImports.iterator.map[CSPDefsFileName]
	}

	private def Set<RCPackage> allImports(BasicPackage p) {
		val LinkedList<BasicPackage> visited = newLinkedList;
		val LinkedList<BasicPackage> notvisited = newLinkedList(p);
		while (!notvisited.empty) {
			val current = notvisited.pop
			visited.add(current)
			val importedPackages = current.imports.map[package].filter [
				it !== null && !notvisited.contains(it) && !visited.contains(it)
			]

			notvisited.addAll(importedPackages)
		}
		return visited.filter(RCPackage).toSet
	}

	private def getPackage(Import it) {
		val importName = importedNamespace.replace("::*", "")
		if(importName === null) throw new RuntimeException("Import with invalid format.")
		val rs = eResource.resourceSet
		rs.resources.flatMap[contents.take(1)].filter(BasicPackage).findFirst[importName.equals(name)]
	}
}
