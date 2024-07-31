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
 * Indicates if an annotation should be pared
 * with another annotation.
 * <p>
 * If this is used within your project, please use the {@link Checker#test(String)} to
 * test for paired annotations. Otherwise, this will not be checked.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Paired {

    /**
     * The annotation that should be prepared
     * with the annotation at runtime.
     *
     * @return The paired annotation.
     */
    Class<? extends Annotation> value();

    /**
     * Contains the method used to check all annotations
     * within this project.
     */
    final class Checker {

        public interface Lambda {
            boolean isAnnotationPresent(final @NotNull Class<? extends Annotation> annotation);
        }

        /**
         * Used to test if the annotation is correctly
         * used within a package.
         * <p>
         * This will check classes, constructors, fields, and methods.
         *
         * @param packageName The packages name.
         * @return True if correctly used.
         */
        public static boolean test(final @NotNull String packageName) {

            // Loop though classes.
            for (final Class<?> clazz : findAllClasses(packageName)) {

                // Check class annotations.
                Checker.test(clazz.getDeclaredAnnotations(), (clazz::isAnnotationPresent));

                // Loop though constructors.
                for (final Constructor<?> constructor : clazz.getConstructors()) {
                    Checker.test(constructor.getDeclaredAnnotations(), (constructor::isAnnotationPresent));
                }

                // Loop though field annotations.
                for (final Field field : clazz.getFields()) {
                    Checker.test(field.getDeclaredAnnotations(), (field::isAnnotationPresent));
                }

                // Loop though method annotations.
                for (final Method method : clazz.getMethods()) {
                    Checker.test(method.getDeclaredAnnotations(), (method::isAnnotationPresent));
                }
            }

            return true;
        }

        private static Set<Class> findAllClasses(String packageName) {
            InputStream stream = ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(packageName.replaceAll("[.]", "/"));
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            return reader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());
        }

        private static Class getClass(String className, String packageName) {
            try {
                return Class.forName(packageName + "."
                        + className.substring(0, className.lastIndexOf('.')));
            } catch (ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        }

        public static boolean test(@NotNull Annotation[] annotations, @NotNull Lambda lambda) {
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
