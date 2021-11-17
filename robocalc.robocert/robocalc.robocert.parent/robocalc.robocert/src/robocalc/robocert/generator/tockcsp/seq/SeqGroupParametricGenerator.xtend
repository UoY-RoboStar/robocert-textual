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
package robocalc.robocert.generator.tockcsp.seq

import com.google.inject.Inject
import robocalc.robocert.generator.intf.core.SpecGroupParametricField
import robocalc.robocert.generator.tockcsp.ll.CSPStructureGenerator
import robocalc.robocert.model.robocert.Sequence
import robocalc.robocert.model.robocert.SequenceGroup
import robocalc.robocert.generator.utils.MemoryFactory
import java.util.stream.Collectors
import robocalc.robocert.generator.tockcsp.memory.ModuleGenerator
import robocalc.robocert.generator.tockcsp.core.TargetGenerator

/**
 * Generates the parametric part of a sequence group.
 *
 * Sequence groups are in-part parameterised by any assignments made to their
 * target's parameterisation.
 * 
 * @author Matt Windsor
 */
class SeqGroupParametricGenerator {
	@Inject CSPStructureGenerator csp
	
	@Inject extension ModuleGenerator
	@Inject extension MemoryFactory
	@Inject extension TargetGenerator
	@Inject extension SequenceGenerator
	

}