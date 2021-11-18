package robocalc.robocert.model.robocert.impl;

import robocalc.robocert.model.robocert.BinaryMessageSet;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.ExplicitEdge;
import robocalc.robocert.model.robocert.ExtensionalMessageSet;
import robocalc.robocert.model.robocert.ImplicitEdge;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.RCModuleTarget;
import robocalc.robocert.model.robocert.RefMessageSet;
import robocalc.robocert.model.robocert.SequenceGroup;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.model.robocert.TargetActor;
import robocalc.robocert.model.robocert.UniverseMessageSet;
import robocalc.robocert.model.robocert.WorldActor;

/**
 * Custom factory that injects 'custom' versions of various RoboCert EClasses,
 * including implementations of derived elements.
 */
public class RoboCertFactoryImplCustom extends RoboCertFactoryImpl {
	@Override
	public WorldActor createWorldActor() {
		return new WorldActorImplCustom();
	}
	
	@Override
	public TargetActor createTargetActor() {
		return new TargetActorImplCustom();
	}
	
	@Override
	public RCModuleTarget createRCModuleTarget() {
		return new RCModuleTargetImplCustom();
	}

	@Override
	public SequenceGroup createSequenceGroup() {
		return new SequenceGroupImplCustom();
	}

	@Override
	public Subsequence createSubsequence() {
		return new SubsequenceImplCustom();
	}

	@Override
	public BinaryMessageSet createBinaryMessageSet() {
		return new BinaryMessageSetImplCustom();
	}
	
	@Override
	public ExtensionalMessageSet createExtensionalMessageSet() {
		return new ExtensionalMessageSetImplCustom();
	}

	@Override
	public UniverseMessageSet createUniverseMessageSet() {
		return new UniverseMessageSetImplCustom();
	}

	@Override
	public RefMessageSet createRefMessageSet() {
		return new RefMessageSetImplCustom();
	}

	@Override
	public ImplicitEdge createImplicitEdge() {
		return new ImplicitEdgeImplCustom();
	}

	@Override
	public ExplicitEdge createExplicitEdge() {
		return new ExplicitEdgeImplCustom();
	}

	@Override
	public Instantiation createInstantiation() {
		return new InstantiationImplCustom();
	}

	@Override
	public ConstAssignment createConstAssignment() {
		return new ConstAssignmentImplCustom();
	}	
}