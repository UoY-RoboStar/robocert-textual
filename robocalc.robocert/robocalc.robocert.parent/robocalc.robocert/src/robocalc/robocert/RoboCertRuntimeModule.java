/*
 * generated by Xtext 2.25.0
 */
package robocalc.robocert;

import org.eclipse.xtext.generator.IOutputConfigurationProvider;

import com.google.inject.Binder;
import com.google.inject.Singleton;

import robocalc.robocert.generator.RoboCertOutputConfigurationProvider;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class RoboCertRuntimeModule extends AbstractRoboCertRuntimeModule {
	  @Override
	  public void configure(Binder binder) {
	    super.configure(binder);
	    binder.bind(IOutputConfigurationProvider.class)
	        .to(RoboCertOutputConfigurationProvider.class)
	        .in(Singleton.class);
	  }
}
