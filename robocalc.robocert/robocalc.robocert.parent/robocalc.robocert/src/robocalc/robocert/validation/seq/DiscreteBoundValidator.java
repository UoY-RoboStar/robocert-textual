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
 *   Matt Windsor - initial definition
 ******************************************************************************/

package robocalc.robocert.validation.seq;

import circus.robocalc.robochart.PrimitiveType;
import circus.robocalc.robochart.Type;
import circus.robocalc.robochart.TypeRef;
import circus.robocalc.robochart.textual.RoboCalcTypeProvider;
import com.google.inject.Inject;
import java.util.function.Function;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.validation.AbstractDeclarativeValidator;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.validation.EValidatorRegistrar;
import robostar.robocert.DiscreteBound;
import robostar.robocert.RoboCertPackage.Literals;

/**
 * Validates the well-formedness conditions on {@link DiscreteBound} elements.
 *
 * @author Matt Windsor
 */
public class DiscreteBoundValidator extends AbstractDeclarativeValidator {
  @Override
  public void register(EValidatorRegistrar registrar) {
    // per discussion in ComposedChecks annotation documentation
  }

  @Inject
  private RoboCalcTypeProvider typeProvider;

  /**
   * Checks that a discrete bound's lower bound is natural.
   * @param bound the discrete bound.
   */
  @Check
  public void checkLowerIsNatural(DiscreteBound bound) {
    final var l = bound.getLower();
    if (l != null)
      checkNat(l, "Lower bound must be of type nat; got %s"::formatted,
          Literals.DISCRETE_BOUND__LOWER, "SDbL1");
  }

  /**
   * Checks that a discrete bound's upper bound is natural.
   * @param bound the discrete bound.
   */
  @Check
  public void checkUpperIsNatural(DiscreteBound bound) {
    final var l = bound.getUpper();
    if (l != null)
      checkNat(l, "Upper bound must be of type nat; got %s"::formatted,
          Literals.DISCRETE_BOUND__UPPER, "SDbU1");
  }

  private void checkNat(EObject it, Function<String, String> errorMessage, EStructuralFeature feature, String code) {
    checkType(it, typeProvider.getNatType(it), errorMessage, feature, code);
  }

  private void checkType(EObject it, Type want, Function<String, String> errorMessage, EStructuralFeature feature, String code) {
    final var got = typeProvider.typeFor(it);
    if (!typeProvider.typeCompatible(got, want))
      error(errorMessage.apply(typeName(got)), feature, code);
  }
  
  private String typeName(Type t) {
	  if (t instanceof TypeRef r)
		  return r.getRef().getName();
	  if (t instanceof PrimitiveType p)
		  return p.getName();
	  return "non-primitive type";
  }

  /*
  The lower of a DiscreteBound must be a natural, if present.
  Variables with modifier VAR referenced in the lower of a DiscreteBound must belong to an enclosing Interaction.
  Variables with modifier CONST referenced in the lower of a DiscreteBound must belong to the parameterisation of the target of the enclosing SpecificationGroup.
  The lower of a DiscreteBound must be present if the upper is absent.
  The upper of a DiscreteBound must be a natural, if present
  Variables with modifier VAR referenced in the upper of a DiscreteBound must belong to an enclosing Interaction.
  Variables with modifier CONST referenced in the upper of a DiscreteBound must belong to the parameterisation of the target of the enclosing SpecificationGroup.
  The upper of a DiscreteBound must be present if the lower is absent.
   */
}
