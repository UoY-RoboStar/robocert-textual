package robostar.robocert.textual.scoping;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import com.google.inject.Inject;

import circus.robocalc.robochart.ControllerDef;
import circus.robocalc.robochart.EnumExp;
import circus.robocalc.robochart.Enumeration;
import circus.robocalc.robochart.NamedElement;
import circus.robocalc.robochart.RCModule;
import circus.robocalc.robochart.RCPackage;
import circus.robocalc.robochart.RoboChartPackage;
import circus.robocalc.robochart.RoboChartPackage.Literals;
import circus.robocalc.robochart.textual.scoping.RoboChartScopeProvider;
import circus.robocalc.robochart.util.RoboChartSwitch;
import robostar.robocert.SpecificationGroup;
import robostar.robocert.util.StreamHelper;
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
	
		/*
		// TODO(@MattWindsor91): this is likely iterating backwards.
		final var cts = containersOf(tgt).toList();
		Collections.reverse(cts);
		
		for (var c : cts) {
			final var old = scope;
						
			scope = new RoboChartSwitch<IScope>() {
				@Override
				public IScope defaultCase(EObject object) {
					return old;
				};
				
				@Override
				public IScope caseRCModule(RCModule mod) {
					final var enums = mod.get
					return Scopes.scopeFor(enums, old)
				}
			}.doSwitch(c);
		}
		
		return scope;
	}
	
	// TODO(@MattWindsor91): move this upstream
	private Stream<EObject> containersOf(EObject e) {
		return Stream.iterate(e, Objects::nonNull, EObject::eContainer);
	}
	*/
}
