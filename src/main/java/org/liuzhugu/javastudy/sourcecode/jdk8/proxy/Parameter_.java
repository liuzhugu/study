package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.annotation.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Parameter_ implements AnnotatedElement {

    private final String name;
    private final int modifiers;
    private final Executable_ Executable_;
    private final int index;

    /**
     * Package-private constructor for {@code Parameter_}.
     *
     * If method Parameter_ data is present in the classfile, then the
     * JVM creates {@code Parameter_} objects directly.  If it is
     * absent, however, then {@code Executable_} uses this constructor
     * to synthesize them.
     *
     * @param name The name of the Parameter_.
     * @param modifiers The Modifier_ flags for the Parameter_.
     * @param Executable_ The Executable_ which defines this Parameter_.
     * @param index The index of the Parameter_.
     */
    Parameter_(String name,
              int modifiers,
              Executable_ Executable_,
              int index) {
        this.name = name;
        this.modifiers = modifiers;
        this.Executable_ = Executable_;
        this.index = index;
    }

    /**
     * Compares based on the Executable_ and the index.
     *
     * @param obj The object to compare.
     * @return Whether or not this is equal to the argument.
     */
    public boolean equals(Object obj) {
        if(obj instanceof Parameter_) {
            Parameter_ other = (Parameter_)obj;
            return (other.Executable_.equals(Executable_) &&
                    other.index == index);
        }
        return false;
    }

    /**
     * Returns a hash code based on the Executable_'s hash code and the
     * index.
     *
     * @return A hash code based on the Executable_'s hash code.
     */
    public int hashCode() {
        return Executable_.hashCode() ^ index;
    }

    /**
     * Returns true if the Parameter_ has a name according to the class
     * file; returns false otherwise. Whether a Parameter_ has a name
     * is determined by the {@literal MethodParameters} attribute of
     * the method which declares the Parameter_.
     *
     * @return true if and only if the Parameter_ has a name according
     * to the class file.
     */
    public boolean isNamePresent() {
        return Executable_.hasRealParameterData() && name != null;
    }

    /**
     * Returns a string describing this Parameter_.  The format is the
     * modifiers for the Parameter_, if any, in canonical order as
     * recommended by <cite>The Java&trade; Language
     * Specification</cite>, followed by the fully- qualified type of
     * the Parameter_ (excluding the last [] if the Parameter_ is
     * variable arity), followed by "..." if the Parameter_ is variable
     * arity, followed by a space, followed by the name of the
     * Parameter_.
     *
     * @return A string representation of the Parameter_ and associated
     * information.
     */
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        final Type type = getParameterizedType();
        final String typename = type.getTypeName();

        sb.append(Modifier_.toString(getModifiers()));

        if(0 != modifiers)
            sb.append(' ');

        if(isVarArgs())
            sb.append(typename.replaceFirst("\\[\\]$", "..."));
        else
            sb.append(typename);

        sb.append(' ');
        sb.append(getName());

        return sb.toString();
    }

    /**
     * Return the {@code Executable_} which declares this Parameter_.
     *
     * @return The {@code Executable_} declaring this Parameter_.
     */
    public Executable_ getDeclaringExecutable() {
        return Executable_;
    }

    /**
     * Get the Modifier_ flags for this the Parameter_ represented by
     * this {@code Parameter_} object.
     *
     * @return The Modifier_ flags for this Parameter_.
     */
    public int getModifiers() {
        return modifiers;
    }

    /**
     * Returns the name of the Parameter_.  If the Parameter_'s name is
     * {@linkplain #isNamePresent() present}, then this method returns
     * the name provided by the class file. Otherwise, this method
     * synthesizes a name of the form argN, where N is the index of
     * the Parameter_ in the descriptor of the method which declares
     * the Parameter_.
     *
     * @return The name of the Parameter_, either provided by the class
     *         file or synthesized if the class file does not provide
     *         a name.
     */
    public String getName() {
        // Note: empty strings as paramete names are now outlawed.
        // The .equals("") is for compatibility with current JVM
        // behavior.  It may be removed at some point.
        if(name == null || name.equals(""))
            return "arg" + index;
        else
            return name;
    }

    // Package-private accessor to the real name field.
    String getRealName() {
        return name;
    }

    /**
     * Returns a {@code Type} object that identifies the parameterized
     * type for the Parameter_ represented by this {@code Parameter_}
     * object.
     *
     * @return a {@code Type} object identifying the parameterized
     * type of the Parameter_ represented by this object
     */
    public Type getParameterizedType() {
        Type tmp = parameterTypeCache;
        if (null == tmp) {
            tmp = Executable_.getAllGenericParameterTypes()[index];
            parameterTypeCache = tmp;
        }

        return tmp;
    }

    private transient volatile Type parameterTypeCache = null;

    /**
     * Returns a {@code Class} object that identifies the
     * declared type for the Parameter_ represented by this
     * {@code Parameter_} object.
     *
     * @return a {@code Class} object identifying the declared
     * type of the Parameter_ represented by this object
     */
    public Class_<?> getType() {
        Class_<?> tmp = parameterClassCache;
        if (null == tmp) {
            tmp = Executable_.getParameterTypes()[index];
            parameterClassCache = tmp;
        }
        return tmp;
    }

    /**
     * Returns an AnnotatedType object that represents the use of a type to
     * specify the type of the formal Parameter_ represented by this Parameter_.
     *
     * @return an {@code AnnotatedType} object representing the use of a type
     *         to specify the type of the formal Parameter_ represented by this
     *         Parameter_
     */
    public AnnotatedType getAnnotatedType() {
        // no caching for now
        return Executable_.getAnnotatedParameterTypes()[index];
    }

    private transient volatile Class_<?> parameterClassCache = null;

    /**
     * Returns {@code true} if this Parameter_ is implicitly declared
     * in source code; returns {@code false} otherwise.
     *
     * @return true if and only if this Parameter_ is implicitly
     * declared as defined by <cite>The Java&trade; Language
     * Specification</cite>.
     */
    public boolean isImplicit() {
        return Modifier_.isMandated(getModifiers());
    }

    /**
     * Returns {@code true} if this Parameter_ is neither implicitly
     * nor explicitly declared in source code; returns {@code false}
     * otherwise.
     *
     * @jls 13.1 The Form of a Binary
     * @return true if and only if this Parameter_ is a synthetic
     * construct as defined by
     * <cite>The Java&trade; Language Specification</cite>.
     */
    public boolean isSynthetic() {
        return Modifier_.isSynthetic(getModifiers());
    }

    /**
     * Returns {@code true} if this Parameter_ represents a variable
     * argument list; returns {@code false} otherwise.
     *
     * @return {@code true} if an only if this Parameter_ represents a
     * variable argument list.
     */
    public boolean isVarArgs() {
        return Executable_.isVarArgs() &&
                index == Executable_.getParameterCount() - 1;
    }


    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        return annotationClass.cast(declaredAnnotations().get(annotationClass));
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);

        return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getDeclaredAnnotations() {
        return Executable_.getParameterAnnotations()[index];
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotation is the same as
        // getAnnotation.
        return getAnnotation(annotationClass);
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
        // Only annotations on classes are inherited, for all other
        // objects getDeclaredAnnotations is the same as
        // getAnnotations.
        return getAnnotationsByType(annotationClass);
    }

    /**
     * {@inheritDoc}
     */
    public Annotation[] getAnnotations() {
        return getDeclaredAnnotations();
    }

    private transient Map<Class<? extends Annotation>, Annotation> declaredAnnotations;

    private synchronized Map<Class<? extends Annotation>, Annotation> declaredAnnotations() {
        if(null == declaredAnnotations) {
            declaredAnnotations =
                    new HashMap<Class<? extends Annotation>, Annotation>();
            Annotation[] ann = getDeclaredAnnotations();
            for(int i = 0; i < ann.length; i++)
                declaredAnnotations.put(ann[i].annotationType(), ann[i]);
        }
        return declaredAnnotations;
    }

}