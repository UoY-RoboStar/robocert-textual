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
package robocalc.robocert.model.robocert.impl;

import robocalc.robocert.model.robocert.BinaryMessageSet;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.EventTopic;
import robocalc.robocert.model.robocert.ExplicitEdge;
import robocalc.robocert.model.robocert.ExtensionalMessageSet;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTopic;
import robocalc.robocert.model.robocert.RefMessageSet;
import robocalc.robocert.model.robocert.SpecificationGroup;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.UniverseMessageSet;
import robocalc.robocert.model.robocert.World;

/**
 * Custom factory that injects 'custom' versions of various RoboCert EClasses, including
 * implementations of derived elements.
 *
 * @author Matt Windsor
 */
public class RoboCertFactoryImplCustom extends RoboCertFactoryImpl {
  @Override
  public BinaryMessageSet createBinaryMessageSet() {
    return new BinaryMessageSetImplCustom();
  }

  @Override
  public ConstAssignment createConstAssignment() {
    return new ConstAssignmentImplCustom();
  }

  @Override
  public World createWorld() {
    return new WorldImplCustom();
  }

  @Override
  public EventTopic createEventTopic() {
    return new EventTopicImplCustom();
  }

  @Override
  public ExplicitEdge createExplicitEdge() {
    return new ExplicitEdgeImplCustom();
  }

  @Override
  public ExtensionalMessageSet createExtensionalMessageSet() {
    return new ExtensionalMessageSetImplCustom();
  }

  @Override
  public ImplicitEdge createImplicitEdge() {
    return new ImplicitEdgeImplCustom();
  }

  @Override
  public Instantiation createInstantiation() {
    return new InstantiationImplCustom();
  }

  @Override
  public ModuleTarget createModuleTarget() {
    return new ModuleTargetImplCustom();
  }

  @Override
  public InModuleTarget createInModuleTarget() {
    return new InModuleTargetImplCustom();
  }

  // TODO(@MattWindsor91): other targets

  @Override
  public OperationTopic createOperationTopic() {
    return new OperationTopicImplCustom();
  }

  @Override
  public RefMessageSet createRefMessageSet() {
    return new RefMessageSetImplCustom();
  }

  @Override
  public SpecificationGroup createSpecificationGroup() {
    return new SpecificationGroupImplCustom();
  }

  @Override
  public TargetActor createTargetActor() {
    return new TargetActorImplCustom();
  }

  @Override
  public UniverseMessageSet createUniverseMessageSet() {
    return new UniverseMessageSetImplCustom();
  }
}
