package robocalc.robocert.generator.tockcsp.memory;

import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

import com.google.inject.Inject;

import robocalc.robocert.generator.tockcsp.core.BindingGenerator;
import robocalc.robocert.model.robocert.Binding;
import robocalc.robocert.model.robocert.BindingExpr;

/**
 * Generates loads and stores between local storage and the memory module.
 *
 * Loads are used to put bindings into scope at the CSP level before they appear
 * in expressions. Stores are used to update the memory with bindings captured
 * in arrow actions.
 *
 * @author Matt Windsor
 */
public class LoadStoreGenerator {
	@Inject
	private BindingGenerator bg;
	@Inject
	private ModuleGenerator mg;

	/**
	 * Generates a memory load prefix for all bindings referenced in child
	 * expressions of this object.
	 *
	 * This should be inserted before the main CSP artefact generated from the
	 * object.
	 *
	 * @param it the object whose memory loads we are generating.
	 *
	 * @return the CSP-M prefix.
	 */
	public CharSequence generateExpressionLoads(EObject it) {
		return generateLoads(getExprBindings(it));
	}

	/**
	 * Generates a memory load prefix for a set of bindings.
	 *
	 * This should be inserted before whatever CSP process is going to reference the
	 * bindings.
	 *
	 * @param bindings the bindings from which we are generating.
	 *
	 * @return the CSP-M prefix.
	 */
	public CharSequence generateLoads(Stream<Binding> bindings) {
		return generatePrefix(bindings, "get?", x -> bg.generateInputName(x));
	}

	/**
	 * Generates a memory store prefix for all bindings included in this object.
	 *
	 * This should be inserted AFTER the CSP prefix that produced the bindings
	 * (between the '->' and the 'SKIP').
	 *
	 * @param it the object whose memory stores we are generating.
	 *
	 * @return the CSP-M prefix.
	 */
	public CharSequence generateBindingStores(EObject it) {
		return generateStores(EcoreUtil2.eAllOfType(it, Binding.class).stream());
	}

	/**
	 * Generates a memory store prefix for a set of bindings.
	 *
	 * This should be inserted AFTER the CSP prefix that produced the bindings
	 * (between the '->' and the 'SKIP').
	 *
	 * @param bindings the bindings from which we are generating.
	 *
	 * @return the CSP-M prefix.
	 */
	public CharSequence generateStores(Stream<Binding> bindings) {
		return generatePrefix(bindings, "set!", x -> bg.generateExpressionName(x));
	}

	/**
	 * Gets all bindings referenced in expressions within this object.
	 *
	 * @param it the object to inspect.
	 *
	 * @return all bindings referenced in expressions within it.
	 */
	public Stream<Binding> getExprBindings(EObject it) {
		return EcoreUtil2.eAllOfType(it, BindingExpr.class).stream().map(BindingExpr::getSource);
	}

	private CharSequence generatePrefix(Stream<Binding> bindings, String operator, Function<Binding, CharSequence> getRhs) {
		return String.join("", bindings.distinct().map(x -> generatePrefixItem(x, operator, getRhs)).toList());
	}

	private CharSequence generatePrefixItem(Binding b, String operator, Function<Binding, CharSequence> getRhs) {
		return "%s.%s%s -> ".formatted(mg.generateChannelRef(b), operator, getRhs.apply(b));
	}
}