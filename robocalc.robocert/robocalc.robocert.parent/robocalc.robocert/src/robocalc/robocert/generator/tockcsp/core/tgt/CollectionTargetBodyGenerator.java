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
 *   Alvaro Miyazawa and Pedro Ribeiro - initial definition in RoboChart
 *   Matt Windsor - port to RoboCert
 ******************************************************************************/

package robocalc.robocert.generator.tockcsp.core.tgt;

import circus.robocalc.robochart.generator.csp.comp.timed.CTimedControllerGenerator;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedGeneratorUtils;
import circus.robocalc.robochart.generator.csp.comp.timed.CTimedModuleGenerator;
import com.google.inject.Inject;
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.CollectionTarget;
import robocalc.robocert.model.robocert.InControllerTarget;
import robocalc.robocert.model.robocert.InModuleTarget;

/**
 * Generates CSP-M for the bodies of collection targets.
 * <p>
 * Unlike component targets (for which we can just use the processes generated by the RoboChart
 * semantics), collection targets have to pry into what would usually be black-box definitions at
 * the RoboChart level.  This means we need to replicate the insides of those definitions here; as
 * such, this generator is heavily based on the generator used for RoboChart module and controller
 * processes.
 *
 * @author Matt Windsor
 */
public record CollectionTargetBodyGenerator(CSPStructureGenerator csp, CTimedGeneratorUtils gu, CTimedControllerGenerator ctrlGen, CTimedModuleGenerator modGen) {

  /**
   * Constructs the generator.
   *
   * @param csp low-level CSP formatter.
   */
  @Inject
  public CollectionTargetBodyGenerator {
    Objects.requireNonNull(csp);
    Objects.requireNonNull(gu);
    Objects.requireNonNull(ctrlGen);
    Objects.requireNonNull(modGen);
  }

  /**
   * Generates CSP-M for a collection target.
   *
   * @param target the target to generate.
   * @return CSP-M for the target definition.
   */
  public CharSequence generate(CollectionTarget target) {
    if (target instanceof InModuleTarget m) {
      return generate(m);
    }
    if (target instanceof InControllerTarget c) {
      return generate(c);
    }
    throw new IllegalArgumentException("unsupported collection target: %s".formatted(target));
  }


  public CharSequence generate(InModuleTarget target) {
    final var module = target.getModule();

    final var body = "SKIP";

    return prioritiseAndHideTermination(body, module);

/*
				D__«params» = prioritise(
					(«IF async.size > 0»let
						«compileBuffers(async, bidirecAsync, m)»
					within
						«ENDIF»
						(
							(
								(«composeBuffers(async, bidirecAsync, m)»)
									[|{|«FOR e: syncset SEPARATOR ","»«e»«ENDFOR»|}|]
								(
									«FOR v: gu.allLocalVariables(rp)»
									«IF v.initial !== null»
									set_«gu.variableId(v)»!«eg.compileExpression(v.initial,m)» ->
									«ENDIF»
									«ENDFOR»
									«m.composeControllers(rp,m.nodes.filter(Controller).toList,m.connections,false, false)»
										[|
											union(
												{|
													«FOR v : gu.allLocalVariables(rp) SEPARATOR ','»
													set_«gu.variableId(v)»
													«ENDFOR»
												|},
												{|
													«FOR c : m.nodes.filter[x|x instanceof Controller && gu.requiredVariables((x as Controller).ctrlDef).size > 0].map[x|x as Controller] SEPARATOR ','»
													«FOR v: gu.requiredVariables(c.ctrlDef) SEPARATOR ','»«gu.ctrlName(c)»::set_EXT_«gu.variableId(v)»«ENDFOR»
													«ENDFOR»
												|}
											)
										|]
									Memory«memg.memoryInstantiation(rp as Context)»
								)
							)
							\ Union({
								{|
									«FOR e : syncset SEPARATOR ","»
									«e»
									«ENDFOR»
								|},
								{|
									«FOR c : m.nodes.filter[x|x instanceof Controller && gu.requiredVariables((x as Controller).ctrlDef).size > 0].map[x|x as Controller] SEPARATOR ','»
									«FOR v: gu.requiredVariables(c.ctrlDef) SEPARATOR ','»«c.name»::set_EXT_«gu.variableId(v)»«ENDFOR»
									«ENDFOR»
								|},
								{|
									«FOR v : gu.allLocalVariables(rp) SEPARATOR ','»
									get_«gu.variableId(v)», set_«gu.variableId(v)»
									«ENDFOR»
								|}
							})
							[|{|terminate|}|>SKIP
						)\{|terminate|}
					),
					<visibleMemoryEvents,{tock}>
				)
 */
  }

  public CharSequence generate(InControllerTarget target) {
    final var ctrl = target.getController();

    final var body = "SKIP";

    return prioritiseAndHideTermination(body, ctrl);
    /*
    				D__«params» = prioritise(wbisim(
					«ctrl.composeStateMachines(ctrl.machines,ctrl.connections, false, false)»
						[|
							union(
								{|
									«FOR v : gu.allLocalVariables(ctrl) SEPARATOR ','»
									set_«gu.variableId(v)»
									«ENDFOR»
								|},
								{|
									«FOR s : ctrl.machines.filter[x | gu.requiredVariables(gu.stmDef(x)).size > 0] SEPARATOR ','»
									«FOR v: gu.requiredVariables(gu.stmDef(s)) SEPARATOR ','»«gu.stmName(s)»::set_EXT_«gu.variableId(v)»«ENDFOR»
									«ENDFOR»
								|}
							)
						|]
					Memory«memg.memoryInstantiation(ctrl as Context)»
				)
				\ union(
					{|
						«FOR v : gu.allLocalVariables(ctrl) SEPARATOR ','»
						set_«gu.variableId(v)», get_«gu.variableId(v)»
						«ENDFOR»
					|},
					{|
						«FOR s : ctrl.machines.filter[x | gu.requiredVariables(gu.stmDef(x)).size > 0] SEPARATOR ','»
						«FOR v: gu.requiredVariables(gu.stmDef(s)) SEPARATOR ','»«gu.stmName(s)»::set_EXT_«gu.variableId(v)»«ENDFOR»
						«ENDFOR»
					|}
				)
				[|{|terminate|}|>SKIP,
				<union(visibleMemoryEvents,{terminate}),{tock}>)
     */
  }

  private CharSequence prioritiseAndHideTermination(CharSequence body, EObject element) {
    final var cs = csp.sets();

    final var terminate = procElement(element, "terminate");
    final var visibleMemoryEvents = procElement(element, "visibleMemoryEvents");
    final var priorities = cs.list(visibleMemoryEvents, cs.set("tock"));
    return csp.function("prioritise", csp.bins().hide(body, cs.set(terminate)), priorities);
  }

  private CharSequence procElement(EObject element, CharSequence name) {
    return csp.namespaced(gu.processId(element), name);
  }
}
