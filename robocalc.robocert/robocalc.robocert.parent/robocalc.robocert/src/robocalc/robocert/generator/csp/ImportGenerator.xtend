package robocalc.robocert.generator.csp

import circus.robocalc.robochart.RCPackage
import org.eclipse.emf.ecore.resource.Resource
import robocalc.robocert.model.robocert.ModuleSequenceTarget
import circus.robocalc.robochart.RCModule

/**
 * A generator that expands out imports for a top-level resource.
 */
class ImportGenerator {
	Resource resource;

	new(Resource resource) {
		this.resource = resource;
	}
	
	/**
	 * Generates imports.
	 * 
	 * @return  the generated imports.
	 */
	def String generateImports() {
		'''
			«FOR p: packages»
				include "«p.fileName»"
			«ENDFOR»
		'''
	}
	
	// Much of this code comes from circus.robocalc.robochart.assertions
	// and circus.robocalc.robochart.generator.csp.
	// When merging this prototype in, we should probably just use the
	// original code.
	
	private def Iterable<RCPackage> getPackages() {
		resource.resourceSet.resources.filter[isPackageWithTargetedModules].flatMap[x|x.contents.take(1)].filter(RCPackage)
	}
	
	private def boolean isPackageWithTargetedModules(Resource r) {
		r !== resource &&
		r.URI.fileExtension == "rct" &&
		r.hasTargetedModules
	}
	
	private def boolean hasTargetedModules(Resource r) {
		// TODO: this is extremely flaky, I need to find out a more robust way
		// of doing it.
		val boundModules = resource.allContents.filter(ModuleSequenceTarget).map[x|x.module];
		r.allContents.filter(RCModule).exists[x|boundModules.contains(x)]
	}
	
	private def String getFileName(RCPackage p) {
		// NOTE: when we stop hijacking coreassertions, this'll just become ".csp"
		'''«p.fileBasename»_coreassertions.csp'''
	}
	
	private def String getFileBasename(RCPackage p) {
		// from GeneratorUtils
		if (p.name === null) {
			"file_"+p.eResource.URI.lastSegment.replace(".rct", "")
		} else {
			p.name.replaceAll("::","_")
		}
	}
}