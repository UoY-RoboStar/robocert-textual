/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.generator.utils.param;

import circus.robocalc.robochart.Variable;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

/**
 * A located target parameter, consisting of both the variable and the model object on which it is
 * a parameter.
 *
 * This record exists because the variable's containing object in the EMF world isn't always the
 * place where we define the parameter in the semantics; for instance, the variable might belong
 * to an interface which is then required by the target.
 *
 * This record, and much of its supporting code, derives from the GeneratorUtils parameterisation
 * code upstream.
 *
 * @author Matt Windsor
 */
public record ConstantParameter(Variable constant, EObject container) implements Parameter {
    /**
     * Constructs a parameter record.
     * @param constant the constant forming the parameter.
     * @param container the effective model object on which the constant is a parameter.
     */
    public ConstantParameter {
        Objects.requireNonNull(constant);
        Objects.requireNonNull(container);
    }

    @Override
    public String prefix() {
        return "const";
    }

    @Override
    public QualifiedName qualifiedName(IQualifiedNameProvider qnp) {
        return qnp.getFullyQualifiedName(container).append(constant.getName());
    }

    @Override
    public Optional<Variable> tryGetConstant() {
        return Optional.of(constant);
    }

}
