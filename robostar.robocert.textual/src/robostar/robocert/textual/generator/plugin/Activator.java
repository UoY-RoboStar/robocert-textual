/*
 * Copyright (c) 2019-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.generator.plugin;

import com.google.common.collect.Maps;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.shared.SharedStateModule;
import org.eclipse.xtext.util.Modules2;
import org.osgi.framework.BundleContext;
import robostar.robocert.textual.RoboCertRuntimeModule;

import java.util.Collections;
import java.util.Map;

/**
 * Activator functionality shared by the various RoboCert plugins.
 *
 * @author Alvaro Miyazawa (original RoboChart definition)
 * @author Matt Windsor
 */
public class Activator extends AbstractUIPlugin {

  public static final String ROBOSTAR_ROBOCERT_TEXTUAL_ROBOCERT = "robostar.robocert.textual.RoboCert";
  private static Activator plugin;

  private static final Logger logger = Logger.getLogger(Activator.class);

  private final Map<String, Injector> injectors = Collections.synchronizedMap(
      Maps.newHashMapWithExpectedSize(1));

  @Override
  public void start(BundleContext context) throws Exception {
    super.start(context);
    plugin = this;
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    injectors.clear();
    plugin = null;
    super.stop(context);
  }

  public Injector getInjector(String language) {
    synchronized (injectors) {
      Injector injector = injectors.get(language);
      if (injector == null) {
        injectors.put(language, injector = createInjector(language));
      }
      return injector;
    }
  }

  protected Injector createInjector(String language) {
    try {
      final var runtimeModule = getRuntimeModule(language);
      final var sharedStateModule = getSharedStateModule();
      final var mergedModule = Modules2.mixin(runtimeModule, sharedStateModule);
      return Guice.createInjector(mergedModule);
    } catch (Exception e) {
      logger.error("Failed to create injector for " + language);
      logger.error(e.getMessage(), e);
      throw new RuntimeException("Failed to create injector for " + language, e);
    }
  }

  protected com.google.inject.Module getRuntimeModule(String grammar) {
    if (ROBOSTAR_ROBOCERT_TEXTUAL_ROBOCERT.equals(grammar)) {
      return new RoboCertRuntimeModule();
    }
    throw new IllegalArgumentException(grammar);
  }

  protected com.google.inject.Module getSharedStateModule() {
    return new SharedStateModule();
  }

  /**
   * Returns the shared instance
   *
   * @return the shared instance
   */
  public static Activator getInstance() {
    return plugin;
  }
}
