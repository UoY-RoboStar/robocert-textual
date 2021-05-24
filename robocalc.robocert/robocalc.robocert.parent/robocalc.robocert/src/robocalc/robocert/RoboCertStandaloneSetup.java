/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert;

import org.eclipse.emf.ecore.EPackage;

import com.google.inject.Injector;

import robocalc.robocert.model.robocert.RobocertPackage;

/**
 * Initialization support for running Xtext languages without Equinox extension registry.
 */
public class RoboCertStandaloneSetup extends RoboCertStandaloneSetupGenerated {

	public static void doSetup() {
		new RoboCertStandaloneSetup().createInjectorAndDoEMFRegistration();
	}
	
	@Override
	public void register(Injector injector) {
		if (!EPackage.Registry.INSTANCE.containsKey(RobocertPackage.eNS_URI)) {
			EPackage.Registry.INSTANCE.put(RobocertPackage.eNS_URI, RobocertPackage.eINSTANCE);
		}
		super.register(injector);
	}
}
