/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.tikz.util;

import com.google.inject.Inject;
import java.util.Objects;
import org.eclipse.emf.ecore.EObject;
import robostar.robocert.ControllerTarget;
import robostar.robocert.InControllerTarget;
import robostar.robocert.InModuleTarget;
import robostar.robocert.ModuleTarget;
import robostar.robocert.OperationTarget;
import robostar.robocert.StateMachineTarget;
import robostar.robocert.Target;
import robostar.robocert.util.RoboCertSwitch;

/**
 * Helper class for emitting the name of parts of a target.
 *
 * @param tikz low-level TikZ command generator.
 * @author Matt Windsor
 */
public record TargetTypeNameGenerator(TikzStructureGenerator tikz) {
  @Inject
  public TargetTypeNameGenerator {
    Objects.requireNonNull(tikz);
  }

  /**
   * Gets a macro for generating the name of a target type.
   * <p>
   * For instance, an in-module target will produce TikZ for showing 'components of module'.
   *
   * @param tgt target for which we are outputting the type.
   * @return TikZ code for displaying the type of the target.
   */
  public String targetTypeName(Target tgt) {
    // TODO(@MattWindsor91): this duplicates the toString on targets!
    return new RoboCertSwitch<String>() {
      @Override
      public String defaultCase(EObject e) {
        return "unknown";
      }

      //
      // Component targets
      //

      @Override
      public String caseModuleTarget(ModuleTarget m) {
        return component("module");
      }

      @Override
      public String caseControllerTarget(ControllerTarget m) {
        return component("controller");
      }


      @Override
      public String caseStateMachineTarget(StateMachineTarget m) {
        return component("state machine");
      }

      @Override
      public String caseOperationTarget(OperationTarget m) {
        return component("operation");
      }

      //
      // Collection targets
      //

      @Override
      public String caseInModuleTarget(InModuleTarget m) {
        return collection("module");
      }

      @Override
      public String caseInControllerTarget(InControllerTarget m) {
        return collection("controller");
      }
    }.doSwitch(tgt);
  }

  private String component(String name) {
    return command("rccomptarget", name);
  }

  private String collection(String name) {
    return command("rccolltarget", name);
  }

  private String command(String cmdName, String name) {
    return tikz.command(cmdName).argument(name).render();
  }
}
