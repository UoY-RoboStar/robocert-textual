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
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.Target
import robocalc.robocert.model.robocert.RCModuleTarget
import java.util.Iterator
import robocalc.robocert.generator.utils.FilenameExtensions

/**
 * A generator that expands out imports for a top-level resource.
 */
class ImportGenerator {
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
		Iterators.concat(
			standardPackageImports,
			referencedElementImports,
			resourcePackageImports,
			importedPackageImports
		).map[toString].toSet
	}
	
	private def standardPackageImports() {
		// TODO(@MattWindsor91): remove instantiations.csp eventually?
		Iterators.forArray(#["defs/robochart_defs.csp", "defs/core_defs.csp", "instantiations.csp"])
	}
	
	private def Iterator<CharSequence> getReferencedElementImports(Resource it) {
		contents.take(1).iterator.flatMap[packageImports]
	}
	
	private def Iterator<CharSequence> getResourcePackageImports(Resource it) {
		// TODO(@MattWindsor91): this may sometimes need to be filtered to
		// anonymous only.
		packages.iterator.map[CSPDefsFileName]
	}
	
	private def Iterator<CharSequence> getImportedPackageImports(Resource it) {
		contents.take(1).filter(BasicPackage).flatMap[allImports].iterator.map[CSPDefsFileName]
	}	
	
	private def dispatch Iterator<CharSequence> getPackageImports(RAPackage it) {
		referencedElements.flatMap[elementImports]
	}
	
	private def dispatch getPackageImports(RCPackage it) {
		Iterators.singletonIterator(CSPMainFileName)
	}
	
	private def Iterator<NamedElement> getReferencedElements(RAPackage it) {
		sequences.iterator.flatMap[referencedElements]
		// TODO(@MattWindsor91): do we need anything else?
	}
	
	private def Iterator<NamedElement> getReferencedElements(Sequence it) {
		target.targetElement
	}
	
	private def dispatch Iterator<NamedElement> getTargetElement(RCModuleTarget it) {
		Iterators.singletonIterator(module)
	}
		
	private def dispatch Iterator<NamedElement> getTargetElement(Target it) {
		Iterators.concat()
	}
	
	private def Iterator<CharSequence> getElementImports(NamedElement element) {
		element.package.iterator.flatMap[
			Iterators.concat(
				Iterators.singletonIterator(CSPDefsFileName),
				Iterators.singletonIterator(element.CSPTopModuleFileName),
				allImports.iterator.map[CSPDefsFileName]
			)
		]
	}
	
	private def Set<RCPackage> allImports(BasicPackage p) {
		val LinkedList<BasicPackage> visited = new LinkedList<BasicPackage>();
		val LinkedList<BasicPackage> notvisited = new LinkedList<BasicPackage>();
		notvisited.add(p);
		while (!notvisited.empty) {
			val current = notvisited.pop
			visited.add(current)
			val importedPackages = current.imports
				.map[i|i.package]
				.filter[x|x !== null]
				.filter[x|!notvisited.contains(x) && !visited.contains(x)]
			
			notvisited.addAll(importedPackages)
		}
		return visited.filter(RCPackage).toSet
	}
	
	private def getPackage(Import i) {
		val importName = i.importedNamespace.replace("::*","")
		if (importName === null) throw new RuntimeException("Import with invalid format.")
		val rs = i.eResource.resourceSet
		rs.resources.filter[r|r.contents.size > 0 && r.contents.get(0) instanceof BasicPackage]
			.map[r | r.contents.get(0) as BasicPackage]
			.findFirst[p | importName.equals(p.name)]
	}
	

}
