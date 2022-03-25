/********************************************************************************
 * Copyright (c) 2019-2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Alvaro Miyazawa - initial definition for RoboChart
 *   Matt Windsor - port to RoboCert
 ********************************************************************************/
package robocalc.robocert;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

import com.google.inject.Injector;

import robocalc.robocert.model.robocert.RoboCertPackage;
import robocalc.robocert.model.robocert.impl.RoboCertFactoryImpl;

/**
 * Initialization support for running Xtext languages without Equinox extension
 * registry.
 */
public class RoboCertStandaloneSetup extends RoboCertStandaloneSetupGenerated {

	public static void doSetup() {
		new RoboCertStandaloneSetup().createInjectorAndDoEMFRegistration();
	}

	@Override
	public void register(Injector injector) {
		if (!EPackage.Registry.INSTANCE.containsKey(RoboCertPackage.eNS_URI))
			EPackage.Registry.INSTANCE.put(RoboCertPackage.eNS_URI, new EPackage.Descriptor() {
				@Override
				public EPackage getEPackage() {
					return RoboCertPackage.eINSTANCE;
				}

				@Override
				public EFactory getEFactory() {
					return new RoboCertFactoryImpl();
				}

			});
		super.register(injector);
	}
}
