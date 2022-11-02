package robostar.robocert.textual.scoping;

import java.util.Objects;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import com.google.inject.Inject;

import circus.robocalc.robochart.EnumExp;
import circus.robocalc.robochart.RoboChartPackage.Literals;
import circus.robocalc.robochart.textual.scoping.RoboChartScopeProvider;
import robostar.robocert.util.resolve.TargetElementResolver;

/**
 * Provides scopes for RoboChart enumerations.
 *
 * @author Matt Windsor
 */
public record EnumScopeProvider(ScopeHelper helper, RoboChartScopeProvider chart, TargetElementResolver tgtResolver) {
	@Inject
	public EnumScopeProvider {
		Objects.requireNonNull(helper);
		Objects.requireNonNull(chart);
	}
	
	public IScope exprScope(EnumExp exp, EReference ref) {
		final var chartScope = chart.getScope(exp, ref);
		
		if (ref == Literals.ENUM_EXP__TYPE) {
			return typeScope(exp, chartScope);
		}
		return chart.getScope(exp, ref);
	}
	
	private IScope typeScope(EnumExp exp, IScope chartScope) {
		/* We want to bring any enumerations available on the target's package
		   of its parents, into scope. */ 
		
		final var grp = helper.specificationGroupOf(exp);
		return grp.map(g -> Scopes.scopeFor(g.getImportedEnums(), chartScope)).orElse(chartScope);
	}
}
