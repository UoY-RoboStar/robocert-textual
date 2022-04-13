package robocalc.robocert.generator.tockcsp.core;

import com.google.inject.Inject;
import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.ll.csp.CSPStructureGenerator;
import robocalc.robocert.generator.utils.name.GroupNamer;
import robocalc.robocert.model.robocert.SpecificationGroup;

/**
 * Abstracts over the act of getting CSP names for specification group components.
 *
 * <p>This mainly exists to break a dependency cycle between the CSP generators and sequence
 * generators.
 */
public class SpecificationGroupElementFinder {
  @Inject private CSPStructureGenerator csp;
  @Inject private GroupNamer gn;

  /**
   * Gets the fully qualified CSP name of a field on a sequence group's parametric part.
   *
   * <p>We assume that we want the closed form of the parametric part.
   *
   * @param it the group whose field is to be located.
   * @param field the field in question.
   * @return CSP-M expanding to a module-qualified name of a field.
   */
  public CharSequence getFullCSPName(SpecificationGroup it, SpecGroupParametricField field) {
    return csp.namespaced(getFullCSPName(it, SpecGroupField.PARAMETRIC_CLOSED), field.toString());
  }

  /**
   * Gets the fully qualified CSP name of a field on a sequence group.
   *
   * @param it the group whose field is to be located.
   * @param field the field in question.
   * @return CSP-M expanding to a module-qualified name of a field.
   */
  public CharSequence getFullCSPName(SpecificationGroup it, SpecGroupField field) {
    return csp.namespaced(gn.getOrSynthesiseName(it), field.toString());
  }
}
