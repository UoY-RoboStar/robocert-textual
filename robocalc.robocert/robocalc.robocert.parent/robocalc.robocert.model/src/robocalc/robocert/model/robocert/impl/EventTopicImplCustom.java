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

import circus.robocalc.robochart.Type;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

/**
 * Inserts derived functionality into {@link EventTopicImpl}.
 *
 * @author Matt Windsor
 */
public class EventTopicImplCustom extends EventTopicImpl {

  @Override
  public EList<Type> getParamTypes() {
    final var out = new BasicEList<Type>(1);
    final var type = getEvent().getType();
    if (type != null) {
      out.add(type);
    }
    return out;
  }
}
