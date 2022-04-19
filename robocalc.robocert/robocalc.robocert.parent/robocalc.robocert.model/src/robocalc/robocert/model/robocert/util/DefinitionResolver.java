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
package robocalc.robocert.model.robocert.util;

import circus.robocalc.robochart.Operation;
import circus.robocalc.robochart.OperationDef;
import circus.robocalc.robochart.OperationRef;
import circus.robocalc.robochart.StateMachine;
import circus.robocalc.robochart.StateMachineBody;
import circus.robocalc.robochart.StateMachineDef;
import circus.robocalc.robochart.StateMachineRef;
import java.util.Optional;
import java.util.stream.Stream;

import circus.robocalc.robochart.ConnectionNode;
import circus.robocalc.robochart.Controller;
import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.ControllerRef;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RoboticPlatform;
import circus.robocalc.robochart.RoboticPlatformDef;
import circus.robocalc.robochart.RoboticPlatformRef;
import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.model.robocert.ControllerTarget;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;
import robocalc.robocert.model.robocert.ModuleTarget;
import robocalc.robocert.model.robocert.OperationTarget;
import robocalc.robocert.model.robocert.StateMachineTarget;
import robocalc.robocert.model.robocert.Target;

/**
 * Helper class for finding definitions of various RoboChart components.
 * 
 * @author Matt Windsor
 */
public class DefinitionResolver {
	//
	// RCModule
	//

	/**
	 * Gets the robotic platform definition for a RoboChart module.
	 *
	 * @param it the RoboChart module.
	 * @return the module's robotic platform, if it has one.
	 */
	public Optional<RoboticPlatformDef> platform(RCModule it) {
		return nodes(it, RoboticPlatform.class).map(this::resolve).findFirst();
	}

	/**
	 * Gets the controller definitions for a RoboChart module.
	 *
	 * @param it the RoboChart module.
	 * @return the module's controllers.
	 */	
	public Stream<ControllerDef> controllers(RCModule it) {
		return nodes(it, Controller.class).map(this::controllerDef);
	}

	private <T extends ConnectionNode> Stream<T> nodes(RCModule m, Class<T> clazz) {
		if (m == null)
			return Stream.empty();
		return StreamHelper.filter(m.getNodes().parallelStream(), clazz);
	}

	//
	// Controllers
	//

	/**
	 * Gets the enclosing module for a RoboChart controller.
	 *
	 * This assumes that the controller is inside a module.
	 *
	 * @param c the RoboChart controller.
	 * @return the controller's module, if it has one.
	 */
	public Optional<RCModule> module(ControllerDef c) {
		return Optional.ofNullable(EcoreUtil2.getContainerOfType(c, RCModule.class));
	}

	//
	// State machine bodies
	//

	/**
	 * Gets the enclosing controller for a RoboChart state machine or operation.
	 *
	 * This assumes that the item is inside a controller.
	 *
	 * @param b the RoboChart state machine body (state machine or operation).
	 * @return the body's controller, if it has one.
	 */
	public Optional<ControllerDef> controller(StateMachineBody b) {
		return Optional.ofNullable(EcoreUtil2.getContainerOfType(b, ControllerDef.class));
	}

	//
	// Targets
	//

	/**
	 * Tries to get the module of a target.
	 * @param target the target in question.
	 * @return the module, if possible.
	 */
	public Optional<RCModule> module(Target target) {
		if (target instanceof InModuleTarget m)
			return Optional.of(m.getModule());
		if (target instanceof ModuleTarget m)
			return Optional.of(m.getModule());
		if (target instanceof InControllerTarget c)
			return module(c.getController());
		if (target instanceof ControllerTarget c)
			return module(c.getController());
		if (target instanceof StateMachineTarget s)
			return controller(s.getStateMachine()).flatMap(this::module);
		if (target instanceof OperationTarget s)
			return controller(s.getOperation()).flatMap(this::module);
		throw new IllegalArgumentException("can't get module of target %s".formatted(target));
	}

	//
	// Resolving
	//

	/**
	 * Resolves a {@link RoboticPlatform} into a {@link RoboticPlatformDef}.
 	 * @param p the robotic platform to resolve.
	 * @return the resolved platform.
	 */
	public RoboticPlatformDef resolve(RoboticPlatform p) {
		if (p instanceof RoboticPlatformDef d)
			return d;
		if (p instanceof RoboticPlatformRef r)
			return r.getRef();
		throw new IllegalArgumentException("expected RoboticPlatform{Def, Ref}, got %s".formatted(p));
	}

	/**
	 * Resolves a {@link Controller} into a {@link ControllerDef}.
	 * @param c the controller to resolve.
	 * @return the resolved operation.
	 */
	private ControllerDef controllerDef(Controller c) {
		if (c instanceof ControllerDef d)
			return d;
		if (c instanceof ControllerRef r)
			return r.getRef();
		throw new IllegalArgumentException("expected Controller{Def, Ref}, got %s".formatted(c));
	}

	/**
	 * Resolves an {@link Operation} into an {@link OperationDef}.
	 * @param op the operation to resolve.
	 * @return the resolved operation.
	 */
	public OperationDef resolve(Operation op) {
		if (op instanceof OperationDef d) {
			return d;
		}
		if (op instanceof OperationRef r) {
			return r.getRef();
		}
		throw new IllegalArgumentException("expected Operation{Def, Ref}, got %s".formatted(op));
	}

	/**
	 * Resolves a {@link StateMachine} into an {@link StateMachineDef}.
	 * @param stm the state machine to resolve.
	 * @return the resolved state machine.
	 */
	public StateMachineDef resolve(StateMachine stm) {
		if (stm instanceof StateMachineDef d) {
			return d;
		}
		if (stm instanceof StateMachineRef r) {
			return r.getRef();
		}
		throw new IllegalArgumentException("expected StateMachine{Def, Ref}, got %s".formatted(stm));
	}
}
