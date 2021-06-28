package robocalc.robocert.generator

import org.eclipse.xtext.generator.IOutputConfigurationProvider
import org.eclipse.xtext.generator.OutputConfiguration
import org.eclipse.xtext.generator.IFileSystemAccess2

/**
 * Overrides output configuration to save CSP to csp-gen.
 */
class RoboCertOutputConfigurationProvider implements IOutputConfigurationProvider {
	
	override getOutputConfigurations() {
		// TODO: is this the right thing to do?
		// TODO: PRISM gen
		
		var defaultOutput = new OutputConfiguration(IFileSystemAccess2.DEFAULT_OUTPUT) => [
			description = "tock-CSP Folder"
    		outputDirectory = "./csp-gen/timed";
    		overrideExistingResources = true;
    		createOutputDirectory = true;
    		cleanUpDerivedResources = true;
    		setDerivedProperty = true;
    		keepLocalHistory = true  		
    	];
    	
    	newHashSet(defaultOutput)
	}
	
}
