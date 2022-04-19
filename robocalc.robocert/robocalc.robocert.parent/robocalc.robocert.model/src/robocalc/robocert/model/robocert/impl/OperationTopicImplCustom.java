/*******************************************************************************
 * Copyright (c) 2021 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.Parameter;
import circus.robocalc.robochart.Type;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.model.robocert.util.StreamHelper;

/**
 * Inserts derived functionality into {@link OperationTopicImpl}.
 *
 * @author Matt Windsor
 */
public class OperationTopicImplCustom extends OperationTopicImpl {
  @Override
  public EList<Type> getParamTypes() {
    return StreamHelper.toEList(getOperation().getParameters().stream().map(Parameter::getType));
  }
}
