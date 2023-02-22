/*
 * Copyright (c) 2022-2023 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.validation;

import org.eclipse.xtext.validation.ComposedChecks;
import robostar.robocert.textual.validation.core.ExpressionValidator;
import robostar.robocert.textual.validation.seq.DiscreteBoundValidator;
import robostar.robocert.textual.validation.seq.ActorValidator;
import robostar.robocert.textual.validation.seq.InteractionValidator;
import robostar.robocert.textual.validation.seq.LifelineValidator;
import robostar.robocert.textual.validation.seq.MessageValidator;
import robostar.robocert.textual.validation.seq.SpecificationGroupValidator;

/**
 * This class contains custom validation rules.
 * <p>
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#validation
 *
 * @author Matt Windsor
 */
@ComposedChecks(validators = {ActorValidator.class, DiscreteBoundValidator.class,
    ExpressionValidator.class, InteractionValidator.class, LifelineValidator.class,
    MessageValidator.class, SpecificationGroupValidator.class})
public class RoboCertValidator extends AbstractRoboCertValidator {

}
