/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

/*
 * generated by Xtext 2.25.0
 */
package robostar.robocert.textual.generator;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.AbstractGenerator;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.generator.IGeneratorContext;

import com.google.inject.Inject;

import robostar.robocert.textual.generator.tikz.TikzGenerator;
import robostar.robocert.textual.generator.tockcsp.TockCspGenerator;

/**
 * Generates code from model files on save.
 * <p>
 * See
 * https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
public class RoboCertGenerator extends AbstractGenerator {
	// TODO(@MattWindsor91): make this open-ended in the same way that the RoboChart generator is.

	private final TockCspGenerator csp;
	private final TikzGenerator tikz;

	@Inject
	public RoboCertGenerator(TockCspGenerator csp, TikzGenerator tikz) {
			this.csp = csp;
			this.tikz = tikz;
	}

	@Override
	public void beforeGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
		// Workaround for resolution errors.
		EcoreUtil.resolveAll(input.getResourceSet());

		forEachGenerator(gen -> gen.beforeGenerate(input, fsa, context), context);
	}

	@Override
	public void doGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
		// Workaround for resolution errors.
		EcoreUtil.resolveAll(input.getResourceSet());

		forEachGenerator(gen -> SafeRunner.run(new ISafeRunnable() {
			@Override
			public void handleException(Throwable e) {
				// TODO(@MattWindsor91): is there a log this should be sent to?
				System.err.println("ERROR: RoboCert generator threw an exception.");
				System.err.println("Please file this as a bug at github.com/UoY-RoboStar/robocert-textual.");
				System.err.println("Details:");
				e.printStackTrace();
			}
			
			@Override
			public void run() throws Exception {
				gen.doGenerate(input, fsa, context);
			}
		}), context);
	}

	@Override
	public void afterGenerate(Resource input, IFileSystemAccess2 fsa, IGeneratorContext context) {
		forEachGenerator(gen -> gen.afterGenerate(input, fsa, context), context);
	}

	/**
	 * Applies the consumer to each registered generator, returning early on cancellation.
	 * @param f function to apply to each generator.
	 * @param context context used to check cancellation.
	 */
	private void forEachGenerator(Consumer<IGenerator2> f, IGeneratorContext context) {
		for (IGenerator2 gen: List.of(csp, tikz)) {
			if (context.getCancelIndicator().isCanceled())
				return;
			f.accept(gen);
		}
	}
}
