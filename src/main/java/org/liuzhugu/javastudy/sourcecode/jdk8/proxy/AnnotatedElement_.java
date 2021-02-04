package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.annotation.AnnotationSupport;
import sun.reflect.annotation.AnnotationType;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface AnnotatedElement_ {
    /**
     * Returns true if an annotation for the specified type
     * is <em>present</em> on this element, else false.  This method
     * is designed primarily for convenient access to marker annotations.
     *
     * <p>The truth value returned by this method is equivalent to:
     * {@code getAnnotation(annotationClass) != null}
     *
     * <p>The body of the default method is specified to be the code
     * above.
     *
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return true if an annotation for the specified annotation
     *     type is present on this element, else false
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return getAnnotation(annotationClass) != null;
    }

    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is <em>present</em>, else null.
     *
     * @param <T> the type of the annotation to query for and return if present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.5
     */
    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    /**
     * Returns annotations that are <em>present</em> on this element.
     *
     * If there are no annotations <em>present</em> on this element, the return
     * value is an array of length 0.
     *
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     *
     * @return annotations present on this element
     * @since 1.5
     */
    Annotation_[] getAnnotations();

   
    default <T extends Annotation_> T[] getAnnotationsByType(Class_<T> annotationClass) {
        /*
         * Definition of associated: directly or indirectly present OR
         * neither directly nor indirectly present AND the element is
         * a Class, the annotation type is inheritable, and the
         * annotation type is associated with the superclass of the
         * element.
         */
        T[] result = getDeclaredAnnotationsByType(annotationClass);

//        if (result.length == 0 && // Neither directly nor indirectly present
//                this instanceof Class_ && // the element is a class
//                AnnotationType.getInstance(annotationClass).isInherited()) { // Inheritable
//            Class_<?> superClass = ((Class_<?>) this).getSuperclass();
//            if (superClass != null) {
//                // Determine if the annotation is associated with the
//                // superclass
//                result = superClass.getAnnotationsByType(annotationClass);
//            }
//        }

        return result;
    }

    /**
     * Returns this element's annotation for the specified type if
     * such an annotation is <em>directly present</em>, else null.
     *
     * This method ignores inherited annotations. (Returns null if no
     * annotations are directly present on this element.)
     *
     * @implSpec The default implementation first performs a null check
     * and then loops over the results of {@link
     * #getDeclaredAnnotations} returning the first annotation whose
     * annotation type matches the argument type.
     *
     * @param <T> the type of the annotation to query for and return if directly present
     * @param annotationClass the Class object corresponding to the
     *        annotation type
     * @return this element's annotation for the specified annotation type if
     *     directly present on this element, else null
     * @throws NullPointerException if the given annotation class is null
     * @since 1.8
     */
    default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        // Loop over all directly-present annotations looking for a matching one
        for (Annotation_ annotation : getDeclaredAnnotations()) {
            if (annotationClass.equals(annotation.annotationType())) {
                // More robust to do a dynamic cast at runtime instead
                // of compile-time only.
                return annotationClass.cast(annotation);
            }
        }
        return null;
    }

    default <T extends Annotation_> T[] getDeclaredAnnotationsByType(Class_<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
//        return AnnotationSupport.
//                getDirectlyAndIndirectlyPresent(Arrays.stream(getDeclaredAnnotations()).
//                                collect(Collectors.toMap(Annotation::annotationType,
//                                        Function.identity(),
//                                        ((first,second) -> first),
//                                        LinkedHashMap::new)),
//                        annotationClass);
        return null;
    }

    /**
     * Returns annotations that are <em>directly present</em> on this element.
     * This method ignores inherited annotations.
     *
     * If there are no annotations <em>directly present</em> on this element,
     * the return value is an array of length 0.
     *
     * The caller of this method is free to modify the returned array; it will
     * have no effect on the arrays returned to other callers.
     *
     * @return annotations directly present on this element
     * @since 1.5
     */
    Annotation_[] getDeclaredAnnotations();
}
