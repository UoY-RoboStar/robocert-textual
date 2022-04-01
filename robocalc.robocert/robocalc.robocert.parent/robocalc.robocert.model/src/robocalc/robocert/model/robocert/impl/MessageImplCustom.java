/*******************************************************************************
 * Copyright (c) 2022 University of York and others
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

import robocalc.robocert.model.robocert.World;

/**
 * Adds custom functionality to {@link MessageImpl}.
 *
 * @author Matt Windsor
 */
public class MessageImplCustom extends MessageImpl {
  @Override
  public boolean isOutbound() {
    return getFrom() instanceof World || getTo() instanceof World;
  }
}
