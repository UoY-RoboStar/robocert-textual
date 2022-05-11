/********************************************************************************
 * Copyright (c) 2021, 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ********************************************************************************/
package robostar.robocert.textual;

import org.eclipse.xtext.common.services.Ecore2XtextTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;

/**
 * Value converter service for RoboCert.
 *
 * <p>This extends the default converter to add a converter for CSP code.
 *
 * @author Matt Windsor
 */
public class RoboCertValueConverterService extends Ecore2XtextTerminalConverters {
  /** @return a converter to deal with low-level interpolations. */
  @ValueConverter(rule = "INTERPOLATE")
  public IValueConverter<String> getInterpolateConverter() {
    return new DelimitedStringConverter("<$", "$>");
  }
}
