/*******************************************************************************
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
 ******************************************************************************/

package robocalc.robocert.generator.utils;

import circus.robocalc.robochart.BasicPackage;
import java.util.stream.Stream;
import org.eclipse.emf.ecore.resource.Resource;
import robocalc.robocert.model.robocert.util.StreamHelper;

/**
 * Extensions for finding RoboStar packages in resources.
 *
 * @author Matt Windsor
 */
public class PackageFinder {

  /**
   * Gets the packages of a particular class in the resource set of an EMF resource, excluding this
   * resource.
   *
   * @param <T> type of packages to look for.
   * @param r the top-level resource.
   * @param clazz class of {@code <T>}.
   * @return the packages available on this resource's set.
   */
  public <T extends BasicPackage> Stream<T> packagesInSiblingResources(Resource r, Class<T> clazz) {
    return r.getResourceSet().getResources().stream()
        .filter(x -> x != r)
        .flatMap(x -> packagesInResource(x, clazz));
  }

  /**
   * Gets a stream of packages in a resource with a particular type.
   *
   * @param <T> type of packages to look for.
   * @param r resource to check for packages.
   * @param clazz class of {@code <T>}.
   * @return the stream of packages inside {@code r}.
   */
  public <T extends BasicPackage> Stream<T> packagesInResource(
      Resource r, Class<T> clazz) {
    return StreamHelper.filter(r.getContents().stream(), clazz);
  }
}
