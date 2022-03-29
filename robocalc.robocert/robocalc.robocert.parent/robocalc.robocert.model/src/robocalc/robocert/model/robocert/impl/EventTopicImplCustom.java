/*******************************************************************************
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
 ******************************************************************************/

package robocalc.robocert.model.robocert.impl;

import circus.robocalc.robochart.Type;
import java.util.Optional;
import org.eclipse.emf.common.util.EList;
import robocalc.robocert.model.robocert.util.StreamHelpers;

/**
 * Inserts derived functionality into {@link EventTopicImpl}.
 *
 * @author Matt Windsor
 */
public class EventTopicImplCustom extends EventTopicImpl {

  @Override
  public EList<Type> getParamTypes() {
    // If the event is well-formed, efrom and eto will have the same type.
    return StreamHelpers.toEList(Optional.ofNullable(getEfrom().getType()).stream());
  }
}
