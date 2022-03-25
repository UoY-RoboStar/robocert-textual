package robocalc.robocert.tests.util;

import com.google.inject.Injector;
import robocalc.robocert.tests.RoboCertInjectorProvider;

/** An extended injector provider that slipstreams in some test utility bindings. */
public class RoboCertCustomInjectorProvider extends RoboCertInjectorProvider {
  @Override
  protected Injector internalCreateInjector() {
    return super.internalCreateInjector().createChildInjector(new RoboCertCustomModule());
  }
}
