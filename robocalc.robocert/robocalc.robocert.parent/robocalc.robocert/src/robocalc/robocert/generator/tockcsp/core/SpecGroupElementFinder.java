package robocalc.robocert.generator.tockcsp.core;

import com.google.inject.Inject;

import robocalc.robocert.generator.intf.core.SpecGroupField;
import robocalc.robocert.generator.intf.core.SpecGroupParametricField;
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator;
import robocalc.robocert.generator.utils.name.GroupNamer;
import robocalc.robocert.model.robocert.Sequence;
import robocalc.robocert.model.robocert.SpecGroup;

/**
 * Abstracts over the act of getting CSP names for sequence components.
 * 
 * This mainly exists to break a dependency cycle between the CSP generators
 * and sequence generators.
 */
public class SpecGroupElementFinder {
	@Inject
	private CSPStructureGenerator csp;
	@Inject
	private GroupNamer gn;
	
	// TODO(@MattWindsor91): move sequence stuff out of here.
	
	/**
	 * Gets the fully qualified CSP name of a sequence, including its group.
	 * 
	 * We assume that we want the closed form of the sequence group.
	 * 
	 * @param it  the sequence to locate.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of the sequence.
	 */
	public CharSequence getFullCSPName(Sequence it) {
		return csp.namespaced(getFullCSPName(it.getGroup(), SpecGroupParametricField.SEQUENCE_MODULE), it.getName());
	}

	/**
	 * Gets the fully qualified CSP name of a field on a sequence group.
	 * 
	 * @param it     the group whose field is to be located.
	 * @param field  the field in question.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of a field.
	 */	
	public CharSequence getFullCSPName(SpecGroup it, SpecGroupField field) {
		return csp.namespaced(gn.getOrSynthesiseName(it), field.toString());
	}
	
	/**
	 * Gets the fully qualified CSP name of a field on a sequence group's
	 * parametric part.
	 * 
	 * We assume that we want the closed form of the parametric part.
	 * 
	 * @param it     the group whose field is to be located.
	 * @param field  the field in question.
	 * 
	 * @return  CSP-M expanding to a module-qualified name of a field.
	 */	
	public CharSequence getFullCSPName(SpecGroup it, SpecGroupParametricField field) {
		return csp.namespaced(gn.getOrSynthesiseName(it), SpecGroupField.PARAMETRIC_CLOSED.toString(), field.toString());
	}
}