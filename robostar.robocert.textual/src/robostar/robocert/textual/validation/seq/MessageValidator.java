/*
 * Copyright (c) 2021-2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package robostar.robocert.textual.validation.seq;

import com.google.inject.Inject;

import circus.robocalc.robochart.Type;
import circus.robocalc.robochart.textual.RoboCalcTypeProvider;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.Actor;
import robostar.robocert.EventTopic;
import robostar.robocert.ExpressionValueSpecification;
import robostar.robocert.Message;
import robostar.robocert.OperationTopic;
import robostar.robocert.WildcardValueSpecification;
import robostar.robocert.RoboCertPackage.Literals;
import robostar.robocert.ValueSpecification;
import robostar.robocert.World;
import robostar.robocert.util.resolve.EventResolver;
import robostar.robocert.util.resolve.ParamTypeResolver;

/**
 * Validates aspects of message specifications.
 *
 * @author Matt Windsor
 */
public class MessageValidator extends AbstractDeclarativeValidator {
 
  @Inject
  private EventResolver eventRes;
  @Inject
  private ParamTypeResolver paramTypeRes;
  @Inject
  private RoboCalcTypeProvider typeProvider;

  // TODO(@MattWindsor91): fix the below
  public static final String EDGE_ACTORS_INDISTINCT = "edgeActorsIndistinct";
  //
  // Message
  //
  public static final String OPERATION_NEEDS_CONTEXT = "operationNeedsContext";
  public static final String OPERATION_FROM_CONTEXT = "operationFromContext";

  public static final String EVENT_TOPIC_HAS_CONNECTION = "SMTp2";

  public static final String HAS_CORRECT_ARGUMENT_COUNT = "SMA1";
  public static final String ARGUMENTS_TYPE_COMPATIBLE = "SMA2";


  /**
   * Checks to see if a message with an event topic corresponds to a connection.
   *
   * @param m message being checked.
   */
  @Check
  public void checkEventTopicHasConnection(Message m) {
    if (!(m.getTopic() instanceof EventTopic e)) {
      return;
    }
    final var from = m.getFrom();
    final var to = m.getTo();
    final var candidates = eventRes.resolve(e, from, to)
        .collect(Collectors.toUnmodifiableSet());

    if (candidates.isEmpty()) {
      if (!m.isOutbound()) {
        // in outbound situations, we don't necessarily have access to the World's connections,
        // so it isn't a well-formedness error to not find a connection.
        error("Event topic is not outbound and does not correspond to any connection",
            Literals.MESSAGE__TOPIC, EVENT_TOPIC_HAS_CONNECTION);
      }
      return;
    }

    if (1 < candidates.size()) {
      error("Event topic corresponds to too many connections", Literals.MESSAGE__TOPIC,
          EVENT_TOPIC_HAS_CONNECTION);
    }
    // TODO(@MattWindsor91):
  }

  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  /**
   * Checks that an edge's general flow is valid.
   *
   * @param m message to check.
   */
  @Check
  public void checkEdgeFlow(Message m) {
    if (EcoreUtil.equals(m.getFrom(), m.getTo())) {
      error(
          "A message cannot mention the same actor at both endpoints",
          Literals.MESSAGE__FROM,
          EDGE_ACTORS_INDISTINCT);
    }
  }

  /**
   * Checks that the flow of an operation message is valid.
   *
   * @param m message to check.
   */
  @Check
  public void checkMessageOperationFlow(Message m) {
    // This check is only relevant for operation topics.
    if (!(m.getTopic() instanceof OperationTopic)) {
      return;
    }

    if (isContext(m.getFrom())) {
      error(
          "Operation messages must not originate from a context",
          Literals.MESSAGE__FROM,
          OPERATION_FROM_CONTEXT);
    }
    if (!isContext(m.getTo())) {
      error(
          "Operation messages must call into a context",
          Literals.MESSAGE__TO,
          OPERATION_NEEDS_CONTEXT);
    }

    // TODO(@MattWindsor91): I think that scoping rules will ensure that
    // there cannot be any operation messages into things that can't be
    // called into from this target, but I'm unsure.
  }

  /**
   * Checks the arguments of a message against their parameters.
   * 
   * @param m message to check.
   */
  @Check
  public void checkArgumentsAgainstParameters(Message m) {
    final var args = m.getArguments();
    final var nargs = args.size();
    final var params = paramTypeRes.resolve(m.getTopic()).toList();
    final var nparams = params.size();

	
    if (nparams != nargs) {
	  error(
        "The arguments of a message must have exactly as many elements as its topic has parameters.",
        Literals.MESSAGE__ARGUMENTS,
        HAS_CORRECT_ARGUMENT_COUNT
	  );
    }
	
    final var safeRange = Math.min(nparams, nargs);
    for (var i = 0; i < safeRange; i++) {
      if (!argumentTypeOk(args.get(i), params.get(i))) {
        error(
          "The arguments of a message must be type-compatible with their corresponding parameters.",
          Literals.MESSAGE__ARGUMENTS,
          ARGUMENTS_TYPE_COMPATIBLE        		
        );
      }
	}
  }

  private boolean argumentTypeOk(ValueSpecification arg, Type ptype) {
	 if (arg instanceof WildcardValueSpecification) {
		 // Wildcards match against any type.
		 return true;
	 }
	 if (arg instanceof ExpressionValueSpecification e) {
		 final var etype = typeProvider.typeFor(e.getExpr());
		 return typeProvider.typeCompatible(etype, ptype);
	 }
	 return false;
  }

  private boolean isContext(Actor a) {
    return a instanceof World;
  }
}
