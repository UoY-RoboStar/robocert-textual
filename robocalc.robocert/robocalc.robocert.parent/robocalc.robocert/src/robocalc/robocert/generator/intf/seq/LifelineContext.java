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
package robocalc.robocert.generator.intf.seq;

import org.eclipse.xtext.EcoreUtil2;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.model.robocert.Actor;

/**
 * A context used for a particular lifeline generation.
 *
 * @param actor           the actor associated with the lifeline.
 * @param dataConstructor the actor's data constructor in the enumeration (if any).
 * @param isSingleton     true if this is the only lifeline in the diagram.
 * @author Matt Windsor
 */
public record LifelineContext(Actor actor, CharSequence dataConstructor, boolean isSingleton) {

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

  /**
   * Is this context for the given actor?
   *
   * @param a the actor to check against.
   * @return true provided that this context is building the lifeline for {@code a}.
   */
  public boolean isForLifeline(Actor a) {
    // TODO(@MattWindsor91): can we just use reference equality?
    return EcoreUtil2.equals(a, actor);
  }

  private CharSequence lifelineDef(CSPStructureGenerator csp, String defName) {
    return csp.function(defName, dataConstructor);
  }

  /**
   * Name of the function used in alphaCSP.
   */
  private static final String ALPHA_FUNCTION = "alpha";

  /**
   * Name of the function used in procCSP.
   */
  private static final String PROC_FUNCTION = "proc";
}
