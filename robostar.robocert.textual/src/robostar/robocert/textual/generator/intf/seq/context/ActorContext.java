/*******************************************************************************
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
 ******************************************************************************/
package robostar.robocert.textual.generator.intf.seq.context;

import org.eclipse.xtext.EcoreUtil2;
import robostar.robocert.textual.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robostar.robocert.Actor;
import robostar.robocert.UntilFragment;

/**
 * A lifeline context tied to a particular actor.
 *
 * @param global          the global context of the interaction.
 * @param actor           the actor associated with the lifeline.
 * @param dataConstructor the actor's data constructor in the enumeration (if any).
 * @author Matt Windsor
 */
public record ActorContext(InteractionContext global, Actor actor,
                           CharSequence dataConstructor) implements LifelineContext {

  /**
   * Constructs a reference to this lifeline's CSP alphabet.
   * <p>
   * This reference will only resolve within the sequence's let-within.
   *
   * @param csp a CSP-M structure generator.
   * @return the resulting CSP.
   */
  public CharSequence alphaCSP(CSPStructureGenerator csp) {
    return lifelineDef(csp, ALPHA_FUNCTION);
  }

  /**
   * Constructs a reference to this lifeline's CSP process.
   * <p>
   * This reference will only resolve within the sequence's let-within.
   *
   * @param csp a CSP-M structure generator.
   * @return the resulting CSP.
   */
  public CharSequence procCSP(CSPStructureGenerator csp) {
    return lifelineDef(csp, PROC_FUNCTION);
  }

  @Override
  public boolean isFor(Actor a) {
    // TODO(@MattWindsor91): can we just use reference equality?
    return EcoreUtil2.equals(a, actor);
  }

  @Override
  public String actorName() {
    return actor.getName();
  }

  @Override
  public int untilIndex(UntilFragment frag) {
    return global.untils().fragments().indexOf(frag);
  }

  private CharSequence lifelineDef(CSPStructureGenerator csp, String defName) {
    return csp.function(defName, dataConstructor);
  }

  /**
   * Name of the function used in alphaCSP.
   */
  public static final String ALPHA_FUNCTION = "alpha";

  /**
   * Name of the function used in procCSP.
   */
  public static final String PROC_FUNCTION = "proc";
}
