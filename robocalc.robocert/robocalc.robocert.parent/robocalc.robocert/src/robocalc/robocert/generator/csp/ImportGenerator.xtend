package robocalc.robocert.generator.csp

import circus.robocalc.robochart.RCPackage
import org.eclipse.emf.ecore.resource.Resource
import robocalc.robocert.model.robocert.RCModuleTarget
import circus.robocalc.robochart.RCModule

/**
 * A generator that expands out imports for a top-level resource.
 */
class ImportGenerator {
	/**
	 * Generates imports.
	 * 
	 * @return  the generated imports.
	 */
	def CharSequence generateImports(Resource resource) '''
		«FOR p : resource.packages»
			include "«p.fileName»"
		«ENDFOR»
	'''

	// Much of this code comes from circus.robocalc.robochart.assertions
	// and circus.robocalc.robochart.generator.csp.
	// When merging this prototype in, we should probably just use the
	// original code.
	private def Iterable<RCPackage> getPackages(Resource parent) {
		parent.resourceSet.resources.filter[r|r.isPackageWithTargetedModules(parent)].flatMap[x|x.contents.take(1)].
			filter(RCPackage)
	}

	private def isPackageWithTargetedModules(Resource r, Resource parent) {
		r !== parent && r.URI.fileExtension == "rct" && r.hasTargetedModules(parent)
	}

	private def hasTargetedModules(Resource r, Resource parent) {
		// TODO: this is extremely flaky, I need to find out a more robust way
		// of doing it.
		val boundModules = parent.allContents.filter(RCModuleTarget).map[x|x.module];
		r.allContents.filter(RCModule).exists[x|boundModules.contains(x)]
	}

	private def getFileName(RCPackage p) // NOTE: when we stop hijacking coreassertions, this'll just become ".csp"
	'''«p.fileBasename»_coreassertions.csp'''

	private def getFileBasename(RCPackage p) {
		// from GeneratorUtils
		if (p.name === null) {
			"file_" + p.eResource.URI.lastSegment.replace(".rct", "")
		} else {
			p.name.replaceAll("::", "_")
		}
	}
}
