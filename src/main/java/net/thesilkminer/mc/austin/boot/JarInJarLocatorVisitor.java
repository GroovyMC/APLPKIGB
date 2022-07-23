/*
 * This file is part of APLP: KIGB, licensed under the MIT License
 *
 * Copyright (c) 2022 TheSilkMiner
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.thesilkminer.mc.austin.boot;

import cpw.mods.jarhandling.SecureJar;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.Manifest;

final class JarInJarLocatorVisitor extends SimpleFileVisitor<Path> {
    private final Predicate<SecureJar> jarVerifier;
    private final Consumer<SecureJar> jarConsumer;
    private final Consumer<Path> pathConsumer;

    private JarInJarLocatorVisitor(final Predicate<SecureJar> jarVerifier, final Consumer<SecureJar> jarConsumer, final Consumer<Path> pathConsumer) {
        this.jarVerifier = jarVerifier;
        this.jarConsumer = jarConsumer;
        this.pathConsumer = pathConsumer;
    }

    static JarInJarLocatorVisitor ofSecure(final Predicate<SecureJar> jarVerifier, final Consumer<SecureJar> jarConsumer) {
        return new JarInJarLocatorVisitor(jarVerifier, jarConsumer, null);
    }

    static JarInJarLocatorVisitor ofPath(final Predicate<SecureJar> jarVerifier, final Consumer<Path> pathConsumer) {
        return new JarInJarLocatorVisitor(jarVerifier, null, pathConsumer);
    }

    static Predicate<SecureJar> manifest(final Predicate<Manifest> manifestVerifier) {
        return it -> manifestVerifier.test(it.moduleDataProvider().getManifest());
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        final FileVisitResult result = super.visitFile(file, attrs);
        if (result != FileVisitResult.CONTINUE) {
            return result;
        }
        final String fileName = file.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            final SecureJar jar = SecureJar.from(file);
            if (this.jarVerifier.test(jar)) {
                if (this.jarConsumer != null) {
                    this.jarConsumer.accept(jar);
                    return FileVisitResult.TERMINATE;
                }
                this.pathConsumer.accept(file);
                return FileVisitResult.CONTINUE;
            }
        }
        return FileVisitResult.CONTINUE;
    }
}
