/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert.generator

import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext
import com.google.inject.Inject
import org.eclipse.xtext.EcoreUtil2

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class RoboCertGenerator extends AbstractGenerator {
	@Inject robocalc.robocert.generator.csp.TopGenerator csp

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		// Needed to make sure that imports in any RoboChart packages
		// referenced by this assertion file get picked up.
		EcoreUtil2.resolveAll(resource.resourceSet)
		
		fsa.generateImport
		fsa.generateFile('assertions.csp', csp.generate(resource))
	}
	
	def private generateImport(IFileSystemAccess2 fsa) {
		fsa.generateFile('defs/'+ROBOCERT_DEFS_NAME, importResourceStream)
	}
	
	def importResourceStream() {
		class.classLoader.getResourceAsStream("lib/semantics/"+ROBOCERT_DEFS_NAME)
	}
	

	static String ROBOCERT_DEFS_NAME = "robocert_defs.csp"
}
