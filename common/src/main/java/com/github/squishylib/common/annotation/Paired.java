/*
 * Java configuration and database library.
 * Copyright (C) 2024  Smuddgge
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.squishylib.common.annotation;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Indicates if an annotation should be pared with
 * another annotation.
 * <p>
 * If this is used within your project, please use the method
 * {@link Checker#isUsedCorrectly(String)} to test for paired annotations.
 * Otherwise, pairs will not be checked.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Paired {

    /**
     * The annotation that should be pared with the
     * targeted annotation.
     *
     * @return The paired annotation.
     */
    Class<? extends Annotation> value();

    /**
     * Provides a way of checking the {@link Paired} annotation
     * within your project.
     */
    final class Checker {

        private interface Lambda {
            boolean isAnnotationPresent(final @NotNull Class<? extends Annotation> annotation);
        }

        /**
         * Used to test if the annotation is correctly
         * used within a package.
         * <p>
         * This will check classes, constructors, fields,
         * and methods.
         *
         * @param packageName The packages name.
         * @return True if correctly used.
         */
        public static boolean isUsedCorrectly(final @NotNull String packageName) {

            // Loop though classes.
            for (final Class<?> clazz : Checker.findAllClasses(packageName)) {

                // Check class annotations.
                boolean classes = Checker.isUsedCorrectly(clazz.getDeclaredAnnotations(), (clazz::isAnnotationPresent));
                if (!classes) return false;

                // Loop though constructors.
                for (final Constructor<?> constructor : clazz.getConstructors()) {
                    boolean constructors = Checker.isUsedCorrectly(constructor.getDeclaredAnnotations(), (constructor::isAnnotationPresent));
                    if (!constructors) return false;
                }

                // Loop though field annotations.
                for (final Field field : clazz.getFields()) {
                    boolean fields = Checker.isUsedCorrectly(field.getDeclaredAnnotations(), (field::isAnnotationPresent));
                    if (!fields) return false;
                }

                // Loop though method annotations.
                for (final Method method : clazz.getMethods()) {
                    boolean methods = Checker.isUsedCorrectly(method.getDeclaredAnnotations(), (method::isAnnotationPresent));
                    if (!methods) return false;
                }
            }

            return true;
        }

        private static @NotNull Set<Class<?>> findAllClasses(final @NotNull String packageName) {
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(
                    packageName.replaceAll("[.]", "/")
            );

            // Is the stream null?
            if (stream == null) throw new RuntimeException(
                    "Could not find classes in package " + packageName + " because the input stream returned null."
            );

            // Create the reader.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            // Return the classes in the package.
            return reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> Checker.getClass(line, packageName))
                    .collect(Collectors.toSet());
        }

        private static @NotNull Class<?> getClass(@NotNull String className, @NotNull String packageName) {
            try {
                return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        private static boolean isUsedCorrectly(@NotNull Annotation[] annotations, @NotNull Lambda lambda) {
            for (final Annotation annotation : annotations) {

                // Check if there is a paired annotation.
                Paired pairedAnnotation = annotation.getClass().getAnnotation(Paired.class);
                if (pairedAnnotation == null) continue;

                // Get paired annotation.
                Class<? extends Annotation> annotationToCheck = pairedAnnotation.value();

                // Check if the annotation is not present.
                if (!lambda.isAnnotationPresent(annotationToCheck)) return false;
            }
            return true;
        }
    }
}
