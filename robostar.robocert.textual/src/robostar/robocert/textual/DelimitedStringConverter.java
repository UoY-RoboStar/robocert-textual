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

import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.AbstractNullSafeConverter;
import org.eclipse.xtext.nodemodel.INode;

/**
 * A value converter that strips fixed delimiters from a string.
 *
 * @author Matt Windsor
 */
public class DelimitedStringConverter extends AbstractNullSafeConverter<String> {
  private final String start;
  private final String end;

  public DelimitedStringConverter(String start, String end) {
    this.start = start;
    this.end = end;
  }

  @Override
  protected String internalToString(String value) {
    return String.join("", start, value, end);
  }

  @Override
  protected String internalToValue(String string, INode node) throws ValueConverterException {
    return string.substring(start.length(), string.length() - end.length());
  }
}
