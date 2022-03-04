/********************************************************************************
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
 ********************************************************************************/
package robocalc.robocert.validation;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robocalc.robocert.model.robocert.Actor;
import robocalc.robocert.model.robocert.Message;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.RoboCertPackage.Literals;
import robocalc.robocert.model.robocert.World;

/**
 * Validates aspects of message specifications.
 *
 * @author Matt Windsor
 */
public class MessageValidator extends AbstractDeclarativeValidator {
  public static final String EDGE_ACTORS_INDISTINCT = "edgeActorsIndistinct";

  //
  // Message
  //
  public static final String OPERATION_NEEDS_CONTEXT = "operationNeedsContext";
  public static final String OPERATION_FROM_CONTEXT = "operationFromContext";

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks that an edge's general flow is valid.
   *
   * @param s the spec to check.
   */
  @Check
  public void checkEdgeFlow(Message s) {
    if (EcoreUtil.equals(s.getFrom(), s.getTo()))
      error(
          "A message cannot mention the same actor at both endpoints",
          Literals.MESSAGE__FROM,
          EDGE_ACTORS_INDISTINCT);
  }

  /**
   * Checks that the flow of an operation message is valid.
   *
   * @param s the spec to check.
   */
  @Check
  public void checkMessageOperationFlow(Message s) {
    // This check is only relevant for operation topics.
    if (!(s.getTopic() instanceof OperationTopic)) return;

    if (isContext(s.getFrom()))
      error(
          "Operation messages must not originate from a context",
          Literals.MESSAGE__FROM,
          OPERATION_FROM_CONTEXT);
    if (!isContext(s.getTo()))
      error(
          "Operation messages must call into a context",
          Literals.MESSAGE__TO,
          OPERATION_NEEDS_CONTEXT);

    // TODO(@MattWindsor91): I think that scoping rules will ensure that
    // there cannot be any operation messages into things that can't be
    // called into from this target, but I'm unsure.
  }

  private boolean isContext(Actor a) {
    return a instanceof World;
  }
}
