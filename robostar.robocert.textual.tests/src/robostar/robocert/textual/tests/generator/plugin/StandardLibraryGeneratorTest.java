/*
 * Copyright (c) 2022 University of York and others
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package robostar.robocert.textual.tests.generator.plugin;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.GeneratorContext;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import robostar.robocert.textual.generator.plugin.StandardLibraryGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Tests the {@link StandardLibraryGenerator}.
 *
 * @author Matt Windsor
 */
public class StandardLibraryGeneratorTest {
    private final StandardLibraryGenerator gen = new StandardLibraryGenerator();

    @Test
    public void testGenerate() {
        gen.setInputDirectory("stdlib");
        gen.setOutputDirectory("lib");
        gen.setOutputConfiguration("Cfg");
        gen.addFiles("foo.txt", "bar.txt");

        final var expected = Set.of(
                new MockFileSystemAccess.Result("lib/foo.txt", "Cfg", "Example standard library file 'foo'"),
                new MockFileSystemAccess.Result("lib/bar.txt", "Cfg", "Example standard library file 'bar'")
                // but not baz.txt
        );

        final var mfsa = new MockFileSystemAccess();
        final var mctx = new GeneratorContext();

        gen.generate(getClass(), mfsa, mctx);

        assertEquals(expected, mfsa.results, "stdlib generator seems to have not generated correct files");
    }

    /**
     * Dummy file system access implementation that logs 'written' files to a set.
     */
    private static class MockFileSystemAccess implements IFileSystemAccess2 {
        private record Result(String fileName, String outputCfgName, String data) {}

        public Set<Result> results = new HashSet<>();

        @Override
        public boolean isFile(String path, String outputConfigurationName) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public boolean isFile(String path) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void generateFile(String fileName, CharSequence contents) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void generateFile(String fileName, String outputConfigurationName, CharSequence contents) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void deleteFile(String fileName) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void deleteFile(String fileName, String outputConfigurationName) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public URI getURI(String path, String outputConfiguration) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public URI getURI(String path) {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public void generateFile(String fileName, String outputCfgName, InputStream content) throws RuntimeIOException {
            try {
                final var data = new String(content.readAllBytes(), StandardCharsets.UTF_8);
                results.add(new Result(fileName, outputCfgName, data));
            } catch (IOException e) {
                throw new RuntimeIOException(e);
            }
        }

        @Override
        public void generateFile(String fileName, InputStream content) throws RuntimeIOException {
            generateFile(fileName, IFileSystemAccess2.DEFAULT_OUTPUT, content);
        }

        @Override
        public InputStream readBinaryFile(String fileName, String outputCfgName) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public InputStream readBinaryFile(String fileName) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public CharSequence readTextFile(String fileName, String outputCfgName) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }

        @Override
        public CharSequence readTextFile(String fileName) throws RuntimeIOException {
            throw new UnsupportedOperationException("not implemented");
        }
    }
}
