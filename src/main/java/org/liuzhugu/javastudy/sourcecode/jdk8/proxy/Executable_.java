package org.liuzhugu.javastudy.sourcecode.jdk8.proxy;

import sun.reflect.generics.repository.ConstructorRepository;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Map;
import java.util.Objects;

public abstract class Executable_ extends AccessibleObject_
        implements Member_, GenericDeclaration_ {
    /*
     * Only grant package-visibility to the constructor.
     */
    Executable_() {}

    /**
     * Accessor method to allow code sharing
     */
    abstract byte[] getAnnotationBytes();

    /**
     * Accessor method to allow code sharing
     */
    abstract Executable_ getRoot();

    /**
     * Does the Executable_ have generic information.
     */
    abstract boolean hasGenericInformation();

    abstract ConstructorRepository getGenericInfo();

    boolean equalParamTypes(Class_<?>[] params1, Class_<?>[] params2) {
        /* Avoid unnecessary cloning */
        if (params1.length == params2.length) {
            for (int i = 0; i < params1.length; i++) {
                if (params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    Annotation[][] parseParameterAnnotations(byte[] parameterAnnotations) {
//        return AnnotationParser_.parseParameterAnnotations(
//                parameterAnnotations,
//                sun.misc.SharedSecrets.getJavaLangAccess().
//                        getConstantPool(getDeclaringClass()),
//                getDeclaringClass());
        return null;
    }

    void separateWithCommas(Class_<?>[] types, StringBuilder sb) {
        for (int j = 0; j < types.length; j++) {
            sb.append(types[j].getTypeName());
            if (j < (types.length - 1))
                sb.append(",");
        }

    }

    void printModifiersIfNonzero(StringBuilder sb, int mask, boolean isDefault) {
        int mod = getModifiers() & mask;

        if (mod != 0 && !isDefault) {
            sb.append(Modifier_.toString(mod)).append(' ');
        } else {
            int access_mod = mod & Modifier_.ACCESS_MODIFIERS;
            if (access_mod != 0)
                sb.append(Modifier_.toString(access_mod)).append(' ');
            if (isDefault)
                sb.append("default ");
            mod = (mod & ~Modifier_.ACCESS_MODIFIERS);
            if (mod != 0)
                sb.append(Modifier_.toString(mod)).append(' ');
        }
    }

    String sharedToString(int modifierMask,
                          boolean isDefault,
                          Class_<?>[] parameterTypes,
                          Class_<?>[] exceptionTypes) {
        try {
            StringBuilder sb = new StringBuilder();

            printModifiersIfNonzero(sb, modifierMask, isDefault);
            specificToStringHeader(sb);

            sb.append('(');
            separateWithCommas(parameterTypes, sb);
            sb.append(')');
            if (exceptionTypes.length > 0) {
                sb.append(" throws ");
                separateWithCommas(exceptionTypes, sb);
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Generate toString header information specific to a method or
     * constructor.
     */
    abstract void specificToStringHeader(StringBuilder sb);

    String sharedToGenericString(int modifierMask, boolean isDefault) {
        try {
            StringBuilder sb = new StringBuilder();

            printModifiersIfNonzero(sb, modifierMask, isDefault);

            TypeVariable_<?>[] typeparms = getTypeParameters();
            if (typeparms.length > 0) {
                boolean first = true;
                sb.append('<');
                for(TypeVariable_<?> typeparm: typeparms) {
                    if (!first)
                        sb.append(',');
                    // Class objects can't occur here; no need to test
                    // and call Class.getName().
                    sb.append(typeparm.toString());
                    first = false;
                }
                sb.append("> ");
            }

            specificToGenericStringHeader(sb);

            sb.append('(');
            Type[] params = getGenericParameterTypes();
            for (int j = 0; j < params.length; j++) {
                String param = params[j].getTypeName();
                if (isVarArgs() && (j == params.length - 1)) // replace T[] with T...
                    param = param.replaceFirst("\\[\\]$", "...");
                sb.append(param);
                if (j < (params.length - 1))
                    sb.append(',');
            }
            sb.append(')');
            Type[] exceptions = getGenericExceptionTypes();
            if (exceptions.length > 0) {
                sb.append(" throws ");
                for (int k = 0; k < exceptions.length; k++) {
                    sb.append((exceptions[k] instanceof Class)?
                            ((Class)exceptions[k]).getName():
                            exceptions[k].toString());
                    if (k < (exceptions.length - 1))
                        sb.append(',');
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "<" + e + ">";
        }
    }

    /**
     * Generate toGenericString header information specific to a
     * method or constructor.
     */
    abstract void specificToGenericStringHeader(StringBuilder sb);

    /**
     * Returns the {@code Class} object representing the class or interface
     * that declares the Executable_ represented by this object.
     */
    //public abstract Class_<?> getDeclaringClass();

    /**
     * Returns the name of the Executable_ represented by this object.
     */
    public abstract String getName();

    /**
     * Returns the Java language {@linkplain Modifier_ modifiers} for
     * the Executable_ represented by this object.
     */
    public abstract int getModifiers();

    /**
     * Returns an array of {@code TypeVariable_} objects that represent the
     * type variables declared by the generic declaration represented by this
     * {@code GenericDeclaration} object, in declaration order.  Returns an
     * array of length 0 if the underlying generic declaration declares no type
     * variables.
     *
     * @return an array of {@code TypeVariable_} objects that represent
     *     the type variables declared by this generic declaration
     * @throws GenericSignatureFormatError if the generic
     *     signature of this generic declaration does not conform to
     *     the format specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     */
    public abstract TypeVariable_<?>[] getTypeParameters();

    /**
     * Returns an array of {@code Class} objects that represent the formal
     * Parameter_ types, in declaration order, of the Executable_
     * represented by this object.  Returns an array of length
     * 0 if the underlying Executable_ takes no parameters.
     *
     * @return the Parameter_ types for the Executable_ this object
     * represents
     */
    public abstract Class_<?>[] getParameterTypes();

    /**
     * Returns the number of formal parameters (whether explicitly
     * declared or implicitly declared or neither) for the Executable_
     * represented by this object.
     *
     * @return The number of formal parameters for the Executable_ this
     * object represents
     */
    public int getParameterCount() {
        throw new AbstractMethodError();
    }

    /**
     * Returns an array of {@code Type} objects that represent the formal
     * Parameter_ types, in declaration order, of the Executable_ represented by
     * this object. Returns an array of length 0 if the
     * underlying Executable_ takes no parameters.
     *
     * <p>If a formal Parameter_ type is a parameterized type,
     * the {@code Type} object returned for it must accurately reflect
     * the actual type parameters used in the source code.
     *
     * <p>If a formal Parameter_ type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of {@code Type}s that represent the formal
     *     Parameter_ types of the underlying Executable_, in declaration order
     * @throws GenericSignatureFormatError
     *     if the generic method signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if any of the Parameter_
     *     types of the underlying Executable_ refers to a non-existent type
     *     declaration
     * @throws MalformedParameterizedTypeException if any of
     *     the underlying Executable_'s Parameter_ types refer to a parameterized
     *     type that cannot be instantiated for any reason
     */
    public Type[] getGenericParameterTypes() {
        if (hasGenericInformation())
            return getGenericInfo().getParameterTypes();
        else
            return getParameterTypes();
    }

    /**
     * Behaves like {@code getGenericParameterTypes}, but returns type
     * information for all parameters, including synthetic parameters.
     */
    Type[] getAllGenericParameterTypes() {
        final boolean genericInfo = hasGenericInformation();

        // Easy case: we don't have generic Parameter_ information.  In
        // this case, we just return the result of
        // getParameterTypes().
        if (!genericInfo) {
            return getParameterTypes();
        } else {
            final boolean realParamData = hasRealParameterData();
            final Type[] genericParamTypes = getGenericParameterTypes();
            final Type[] nonGenericParamTypes = getParameterTypes();
            final Type[] out = new Type[nonGenericParamTypes.length];
            final Parameter_[] params = getParameters();
            int fromidx = 0;
            // If we have real Parameter_ data, then we use the
            // synthetic and mandate flags to our advantage.
            if (realParamData) {
                for (int i = 0; i < out.length; i++) {
                    final Parameter_ param = params[i];
                    if (param.isSynthetic() || param.isImplicit()) {
                        // If we hit a synthetic or mandated Parameter_,
                        // use the non generic Parameter_ info.
                        out[i] = nonGenericParamTypes[i];
                    } else {
                        // Otherwise, use the generic Parameter_ info.
                        out[i] = genericParamTypes[fromidx];
                        fromidx++;
                    }
                }
            } else {
                // Otherwise, use the non-generic Parameter_ data.
                // Without method Parameter_ reflection data, we have
                // no way to figure out which parameters are
                // synthetic/mandated, thus, no way to match up the
                // indexes.
                return genericParamTypes.length == nonGenericParamTypes.length ?
                        genericParamTypes : nonGenericParamTypes;
            }
            return out;
        }
    }

    /**
     * Returns an array of {@code Parameter_} objects that represent
     * all the parameters to the underlying Executable_ represented by
     * this object.  Returns an array of length 0 if the Executable_
     * has no parameters.
     *
     * <p>The parameters of the underlying Executable_ do not necessarily
     * have unique names, or names that are legal identifiers in the
     * Java programming language (JLS 3.8).
     *
     * @throws MalformedParametersException if the class file contains
     * a MethodParameters attribute that is improperly formatted.
     * @return an array of {@code Parameter_} objects representing all
     * the parameters to the Executable_ this object represents.
     */
    public Parameter_[] getParameters() {
        // TODO: This may eventually need to be guarded by security
        // mechanisms similar to those in Field, Method, etc.
        //
        // Need to copy the cached array to prevent users from messing
        // with it.  Since parameters are immutable, we can
        // shallow-copy.
        return privateGetParameters().clone();
    }

    private Parameter_[] synthesizeAllParams() {
        final int realparams = getParameterCount();
        final Parameter_[] out = new Parameter_[realparams];
        for (int i = 0; i < realparams; i++)
            // TODO: is there a way to synthetically derive the
            // modifiers?  Probably not in the general case, since
            // we'd have no way of knowing about them, but there
            // may be specific cases.
            out[i] = new Parameter_("arg" + i, 0, this, i);
        return out;
    }

    private void verifyParameters(final Parameter_[] parameters) {
        final int mask = Modifier_.FINAL | Modifier_.SYNTHETIC | Modifier_.MANDATED;

        if (getParameterTypes().length != parameters.length)
            throw new MalformedParametersException("Wrong number of parameters in MethodParameters attribute");

        for (Parameter_ Parameter_ : parameters) {
            final String name = Parameter_.getRealName();
            final int mods = Parameter_.getModifiers();

            if (name != null) {
                if (name.isEmpty() || name.indexOf('.') != -1 ||
                        name.indexOf(';') != -1 || name.indexOf('[') != -1 ||
                        name.indexOf('/') != -1) {
                    throw new MalformedParametersException("Invalid Parameter_ name \"" + name + "\"");
                }
            }

            if (mods != (mods & mask)) {
                throw new MalformedParametersException("Invalid Parameter_ modifiers");
            }
        }
    }

    private Parameter_[] privateGetParameters() {
        // Use tmp to avoid multiple writes to a volatile.
        Parameter_[] tmp = parameters;

        if (tmp == null) {

            // Otherwise, go to the JVM to get them
            try {
                tmp = getParameters0();
            } catch(IllegalArgumentException e) {
                // Rethrow ClassFormatErrors
                throw new MalformedParametersException("Invalid constant pool index");
            }

            // If we get back nothing, then synthesize parameters
            if (tmp == null) {
                hasRealParameterData = false;
                tmp = synthesizeAllParams();
            } else {
                hasRealParameterData = true;
                verifyParameters(tmp);
            }

            parameters = tmp;
        }

        return tmp;
    }

    boolean hasRealParameterData() {
        // If this somehow gets called before parameters gets
        // initialized, force it into existence.
        if (parameters == null) {
            privateGetParameters();
        }
        return hasRealParameterData;
    }

    private transient volatile boolean hasRealParameterData;
    private transient volatile Parameter_[] parameters;

    private native Parameter_[] getParameters0();
    native byte[] getTypeAnnotationBytes0();

    // Needed by reflectaccess
    byte[] getTypeAnnotationBytes() {
        return getTypeAnnotationBytes0();
    }

    /**
     * Returns an array of {@code Class} objects that represent the
     * types of exceptions declared to be thrown by the underlying
     * Executable_ represented by this object.  Returns an array of
     * length 0 if the Executable_ declares no exceptions in its {@code
     * throws} clause.
     *
     * @return the exception types declared as being thrown by the
     * Executable_ this object represents
     */
    public abstract Class_<?>[] getExceptionTypes();

    /**
     * Returns an array of {@code Type} objects that represent the
     * exceptions declared to be thrown by this Executable_ object.
     * Returns an array of length 0 if the underlying Executable_ declares
     * no exceptions in its {@code throws} clause.
     *
     * <p>If an exception type is a type variable or a parameterized
     * type, it is created. Otherwise, it is resolved.
     *
     * @return an array of Types that represent the exception types
     *     thrown by the underlying Executable_
     * @throws GenericSignatureFormatError
     *     if the generic method signature does not conform to the format
     *     specified in
     *     <cite>The Java&trade; Virtual Machine Specification</cite>
     * @throws TypeNotPresentException if the underlying Executable_'s
     *     {@code throws} clause refers to a non-existent type declaration
     * @throws MalformedParameterizedTypeException if
     *     the underlying Executable_'s {@code throws} clause refers to a
     *     parameterized type that cannot be instantiated for any reason
     */
    public Type[] getGenericExceptionTypes() {
        Type[] result;
        if (hasGenericInformation() &&
                ((result = getGenericInfo().getExceptionTypes()).length > 0))
            return result;
        else
            return getExceptionTypes();
    }

    /**
     * Returns a string describing this {@code Executable_}, including
     * any type parameters.
     * @return a string describing this {@code Executable_}, including
     * any type parameters
     */
    public abstract String toGenericString();

    /**
     * Returns {@code true} if this Executable_ was declared to take a
     * variable number of arguments; returns {@code false} otherwise.
     *
     * @return {@code true} if an only if this Executable_ was declared
     * to take a variable number of arguments.
     */
    public boolean isVarArgs()  {
        return (getModifiers() & Modifier_.VARARGS) != 0;
    }

    /**
     * Returns {@code true} if this Executable_ is a synthetic
     * construct; returns {@code false} otherwise.
     *
     * @return true if and only if this Executable_ is a synthetic
     * construct as defined by
     * <cite>The Java&trade; Language Specification</cite>.
     * @jls 13.1 The Form of a Binary
     */
    public boolean isSynthetic() {
        return Modifier_.isSynthetic(getModifiers());
    }

    /**
     * Returns an array of arrays of {@code Annotation}s that
     * represent the annotations on the formal parameters, in
     * declaration order, of the {@code Executable_} represented by
     * this object.  Synthetic and mandated parameters (see
     * explanation below), such as the outer "this" Parameter_ to an
     * inner class constructor will be represented in the returned
     * array.  If the Executable_ has no parameters (meaning no formal,
     * no synthetic, and no mandated parameters), a zero-length array
     * will be returned.  If the {@code Executable_} has one or more
     * parameters, a nested array of length zero is returned for each
     * Parameter_ with no annotations. The annotation objects contained
     * in the returned arrays are serializable.  The caller of this
     * method is free to modify the returned arrays; it will have no
     * effect on the arrays returned to other callers.
     *
     * A compiler may add extra parameters that are implicitly
     * declared in source ("mandated"), as well as parameters that
     * are neither implicitly nor explicitly declared in source
     * ("synthetic") to the Parameter_ list for a method.  See {@link
     * @return an array of arrays that represent the annotations on
     *    the formal and implicit parameters, in declaration order, of
     *    the Executable_ represented by this object
     */
    public abstract Annotation[][] getParameterAnnotations();

    Annotation[][] sharedGetParameterAnnotations(Class_<?>[] parameterTypes,
                                                 byte[] parameterAnnotations) {
        int numParameters = parameterTypes.length;
        if (parameterAnnotations == null)
            return new Annotation[numParameters][0];

        Annotation[][] result = parseParameterAnnotations(parameterAnnotations);

        if (result.length != numParameters)
            handleParameterNumberMismatch(result.length, numParameters);
        return result;
    }

    abstract void handleParameterNumberMismatch(int resultLength, int numParameters);

    /**
     * {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
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
    public <T extends Annotation_> T[] getAnnotationsByType(Class_<T> annotationClass) {
        Objects.requireNonNull(annotationClass);

        //return AnnotationSupport.getDirectlyAndIndirectlyPresent(declaredAnnotations(), annotationClass);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Annotation_[] getDeclaredAnnotations()  {
        return AnnotationParser_.toArray(declaredAnnotations());
    }

    private transient Map<Class_<? extends Annotation_>, Annotation_> declaredAnnotations;

    private synchronized  Map<Class_<? extends Annotation_>, Annotation_> declaredAnnotations() {
        if (declaredAnnotations == null) {
            Executable_ root = getRoot();
            if (root != null) {
                declaredAnnotations = root.declaredAnnotations();
            } else {
//                declaredAnnotations = AnnotationParser_.parseAnnotations(
//                        getAnnotationBytes(),
//                        sun.misc.SharedSecrets.getJavaLangAccess().
//                                getConstantPool(getDeclaringClass()),
//                        getDeclaringClass());
            }
        }
        return declaredAnnotations;
    }

    /**
     * Returns an {@code AnnotatedType} object that represents the use of a type to
     * specify the return type of the method/constructor represented by this
     * Executable_.
     *
     * If this {@code Executable_} object represents a constructor, the {@code
     * AnnotatedType} object represents the type of the constructed object.
     *
     * If this {@code Executable_} object represents a method, the {@code
     * AnnotatedType} object represents the use of a type to specify the return
     * type of the method.
     *
     * @return an object representing the return type of the method
     * or constructor represented by this {@code Executable_}
     */
    public abstract AnnotatedType getAnnotatedReturnType();

    /* Helper for subclasses of Executable_.
     *
     * Returns an AnnotatedType object that represents the use of a type to
     * specify the return type of the method/constructor represented by this
     * Executable_.
     */
    AnnotatedType getAnnotatedReturnType0(Type returnType) {
//        return TypeAnnotationParser.buildAnnotatedType(getTypeAnnotationBytes0(),
//                sun.misc.SharedSecrets.getJavaLangAccess().
//                        getConstantPool(getDeclaringClass()),
//                this,
//                getDeclaringClass(),
//                returnType,
//                TypeAnnotation.TypeAnnotationTarget.METHOD_RETURN);
        return null;
    }




    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * of types to specify formal Parameter_ types of the method/constructor
     * represented by this Executable_. The order of the objects in the array
     * corresponds to the order of the formal Parameter_ types in the
     * declaration of the method/constructor.
     *
     * Returns an array of length 0 if the method/constructor declares no
     * parameters.
     *
     * @return an array of objects representing the types of the
     * formal parameters of the method or constructor represented by this
     * {@code Executable_}
     */
    public AnnotatedType[] getAnnotatedParameterTypes() {
//        return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(),
//                sun.misc.SharedSecrets.getJavaLangAccess().
//                        getConstantPool(getDeclaringClass()),
//                this,
//                getDeclaringClass(),
//                getAllGenericParameterTypes(),
//                TypeAnnotation.TypeAnnotationTarget.METHOD_FORMAL_PARAMETER);
        return null;
    }

    /**
     * Returns an array of {@code AnnotatedType} objects that represent the use
     * of types to specify the declared exceptions of the method/constructor
     * represented by this Executable_. The order of the objects in the array
     * corresponds to the order of the exception types in the declaration of
     * the method/constructor.
     *
     * Returns an array of length 0 if the method/constructor declares no
     * exceptions.
     *
     * @return an array of objects representing the declared
     * exceptions of the method or constructor represented by this {@code
     * Executable_}
     */
    public AnnotatedType[] getAnnotatedExceptionTypes() {
//        return TypeAnnotationParser.buildAnnotatedTypes(getTypeAnnotationBytes0(),
//                sun.misc.SharedSecrets.getJavaLangAccess().
//                        getConstantPool(getDeclaringClass()),
//                this,
//                getDeclaringClass(),
//                getGenericExceptionTypes(),
//                TypeAnnotation.TypeAnnotationTarget.THROWS);
        return null;
    }

}
