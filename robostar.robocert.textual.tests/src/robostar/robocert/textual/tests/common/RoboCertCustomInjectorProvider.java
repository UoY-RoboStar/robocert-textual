/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.common;

import com.google.inject.Injector;
import robostar.robocert.textual.tests.RoboCertInjectorProvider;

/** An extended injector provider that slipstreams in some test utility bindings. */
public class RoboCertCustomInjectorProvider extends RoboCertInjectorProvider {
  @Override
  protected Injector internalCreateInjector() {
    return super.internalCreateInjector().createChildInjector(new RoboCertCustomModule());
  }
}
