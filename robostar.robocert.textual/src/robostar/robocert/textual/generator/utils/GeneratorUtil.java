/*
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
 */

package robostar.robocert.textual.generator.utils;

import com.google.common.collect.Iterators;
import java.util.Iterator;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.generator.IGeneratorContext;
import robostar.robocert.CertPackage;

/**
 * Static helper functions for generators.
 *
 * @author Matt Windsor
 */
public class GeneratorUtil {

  /**
   * Feeds each package in a resource into a consumer function, stopping on cancellation.
   *
   * @param input   input resource to search for packages.
   * @param context context to monitor for cancellation.
   * @param f       consumer function to apply to the packages.
   */
  public static void forEachPackage(Resource input, IGeneratorContext context,
      Consumer<CertPackage> f) {
    final var iter = packages(input);
    while (iter.hasNext()) {
      if (context.getCancelIndicator().isCanceled()) {
        return;
      }

      f.accept(iter.next());
    }
  }

  /**
   * Gets the packages contained within the given resource.
   *
   * @param input resource to inspect.
   * @return an iterator of all RoboCert packages in that resource.
   */
  public static Iterator<CertPackage> packages(Resource input) {
    return contentsOfType(input, CertPackage.class);
  }

  /**
   * Gets the objects of a given type contained within the given resource.
   *
   * @param input  resource to inspect.
   * @param tClass class to extract.
   * @return an iterator of all objects of that class in that resource.
   */
  public static <T> Iterator<T> contentsOfType(Resource input, Class<T> tClass) {
    return Iterators.filter(EcoreUtil.getAllContents(input, true), tClass);
  }
}
