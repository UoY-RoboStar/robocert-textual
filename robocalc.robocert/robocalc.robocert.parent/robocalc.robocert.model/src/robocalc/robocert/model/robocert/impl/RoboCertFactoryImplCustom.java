package robocalc.robocert.model.robocert.impl;

import robocalc.robocert.model.robocert.BinaryMessageSet;
import robocalc.robocert.model.robocert.ConstAssignment;
import robocalc.robocert.model.robocert.DirectionalActorPair;
import robocalc.robocert.model.robocert.ExplicitActorPair;
import robocalc.robocert.model.robocert.ExtensionalMessageSet;
import robocalc.robocert.model.robocert.ImplicitActorPair;
import robocalc.robocert.model.robocert.Instantiation;
import robocalc.robocert.model.robocert.RCModuleTarget;
import robocalc.robocert.model.robocert.RefMessageSet;
import robocalc.robocert.model.robocert.Sequence;
import robocalc.robocert.model.robocert.StandardActor;
import robocalc.robocert.model.robocert.Subsequence;
import robocalc.robocert.model.robocert.UniverseMessageSet;

/**
 * Custom factory that injects 'custom' versions of various RoboCert EClasses,
 * including implementations of derived elements.
 */
public class RoboCertFactoryImplCustom extends RoboCertFactoryImpl {
	@Override
	public RCModuleTarget createRCModuleTarget() {
		return new RCModuleTargetImplCustom();
	}

	@Override
	public StandardActor createStandardActor() {
		return new StandardActorImplCustom();
	}
	
	@Override
	public Sequence createSequence() {
		return new SequenceImplCustom();
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
	public DirectionalActorPair createDirectionalActorPair() {
		return new DirectionalActorPairImplCustom();
	}

	@Override
	public ExplicitActorPair createExplicitActorPair() {
		return new ExplicitActorPairImplCustom();
	}

	@Override
	public ImplicitActorPair createImplicitActorPair() {
		return new ImplicitActorPairImplCustom();
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