package net.bytebuddy.description.method;

import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.ModifierReviewable;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.description.type.generic.GenericTypeDescription;
import net.bytebuddy.description.type.generic.GenericTypeList;
import net.bytebuddy.description.type.generic.TypeVariableSource;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.utility.JavaInstance;
import net.bytebuddy.utility.JavaType;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericSignatureFormatError;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Implementations of this interface describe a Java method, i.e. a method or a constructor. Implementations of this
 * interface must provide meaningful {@code equal(Object)} and {@code hashCode()} implementations.
 */
public interface MethodDescription extends TypeVariableSource,
        NamedElement.WithGenericName,
        ByteCodeElement.TypeDependant<MethodDescription.InDefinedShape, MethodDescription.Token> {

    /**
     * The internal name of a Java constructor.
     */
    String CONSTRUCTOR_INTERNAL_NAME = "<init>";

    /**
     * The internal name of a Java static initializer.
     */
    String TYPE_INITIALIZER_INTERNAL_NAME = "<clinit>";

    /**
     * The type initializer of any representation of a type initializer.
     */
    int TYPE_INITIALIZER_MODIFIER = Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_SYNTHETIC;

    /**
     * Represents a non-defined default value of an annotation property.
     */
    Object NO_DEFAULT_VALUE = null;

    /**
     * Represents any undefined property of a type description that is instead represented as {@code null} in order
     * to resemble the Java reflection API which returns {@code null} and is intuitive to many Java developers.
     */
    MethodDescription UNDEFINED = null;

    /**
     * Returns the return type of the described method.
     *
     * @return The return type of the described method.
     */
    GenericTypeDescription getReturnType();

    /**
     * Returns a list of this method's parameters.
     *
     * @return A list of this method's parameters.
     */
    ParameterList<?> getParameters();

    /**
     * Returns the exception types of the described method.
     *
     * @return The exception types of the described method.
     */
    GenericTypeList getExceptionTypes();

    /**
     * Returns this method modifier but adjusts its state of being abstract.
     *
     * @param nonAbstract {@code true} if the method should be treated as non-abstract.
     * @return The adjusted modifiers.
     */
    int getAdjustedModifiers(boolean nonAbstract);

    /**
     * Checks if this method description represents a constructor.
     *
     * @return {@code true} if this method description represents a constructor.
     */
    boolean isConstructor();

    /**
     * Checks if this method description represents a method, i.e. not a constructor or a type initializer.
     *
     * @return {@code true} if this method description represents a method.
     */
    boolean isMethod();

    /**
     * Checks if this method is a type initializer.
     *
     * @return {@code true} if this method description represents a type initializer.
     */
    boolean isTypeInitializer();

    /**
     * Verifies if a method description represents a given loaded method.
     *
     * @param method The method to be checked.
     * @return {@code true} if this method description represents the given loaded method.
     */
    boolean represents(Method method);

    /**
     * Verifies if a method description represents a given loaded constructor.
     *
     * @param constructor The constructor to be checked.
     * @return {@code true} if this method description represents the given loaded constructor.
     */
    boolean represents(Constructor<?> constructor);

    /**
     * Verifies if this method describes a virtual method, i.e. a method that is inherited by a sub type of this type.
     *
     * @return {@code true} if this method is virtual.
     */
    boolean isVirtual();

    /**
     * Returns the size of the local variable array that is required for this method, i.e. the size of all parameters
     * if they were loaded on the stack including a reference to {@code this} if this method represented a non-static
     * method.
     *
     * @return The size of this method on the operand stack.
     */
    int getStackSize();

    /**
     * Checks if this method represents a Java 8+ default method.
     *
     * @return {@code true} if this method is a default method.
     */
    boolean isDefaultMethod();

    /**
     * Checks if this method can be called using the {@code INVOKESPECIAL} for a given type.
     *
     * @param typeDescription The type o
     * @return {@code true} if this method can be called using the {@code INVOKESPECIAL} instruction
     * using the given type.
     */
    boolean isSpecializableFor(TypeDescription typeDescription);

    /**
     * Returns the default value of this method or {@code null} if no such value exists. The returned values might be
     * of a different type than usual:
     * <ul>
     * <li>{@link java.lang.Class} values are represented as
     * {@link TypeDescription}s.</li>
     * <li>{@link java.lang.annotation.Annotation} values are represented as
     * {@link AnnotationDescription}s</li>
     * <li>{@link java.lang.Enum} values are represented as
     * {@link net.bytebuddy.description.enumeration.EnumerationDescription}s.</li>
     * <li>Arrays of the latter types are represented as arrays of the named wrapper types.</li>
     * </ul>
     *
     * @return The default value of this method or {@code null}.
     */
    Object getDefaultValue();

    /**
     * Returns the default value but casts it to the given type. If the type differs from the value, a
     * {@link java.lang.ClassCastException} is thrown.
     *
     * @param type The type to cast the default value to.
     * @param <T>  The type to cast the default value to.
     * @return The casted default value.
     */
    <T> T getDefaultValue(Class<T> type);

    /**
     * Asserts if this method is invokable on an instance of the given type, i.e. the method is an instance method or
     * a constructor and the method is visible to the type and can be invoked on the given instance.
     *
     * @param typeDescription The type to check.
     * @return {@code true} if this method is invokable on an instance of the given type.
     */
    boolean isInvokableOn(TypeDescription typeDescription);

    /**
     * Checks if the method is a bootstrap method.
     *
     * @return {@code true} if the method is a bootstrap method.
     */
    boolean isBootstrap();

    /**
     * Checks if the method is a bootstrap method that accepts the given arguments.
     *
     * @param arguments The arguments that the bootstrap method is expected to accept where primitive values
     *                  are to be represented as their wrapper types, loaded types by {@link TypeDescription},
     *                  method handles by {@link net.bytebuddy.utility.JavaInstance.MethodHandle} instances and
     *                  method types by {@link net.bytebuddy.utility.JavaInstance.MethodType} instances.
     * @return {@code true} if the method is a bootstrap method that accepts the given arguments.
     */
    boolean isBootstrap(List<?> arguments);

    /**
     * Checks if this method is capable of defining a default annotation value.
     *
     * @return {@code true} if it is possible to define a default annotation value for this method.
     */
    boolean isDefaultValue();

    /**
     * Checks if the given value can describe a default annotation value for this method.
     *
     * @param value The value that describes the default annotation value for this method.
     * @return {@code true} if the given value can describe a default annotation value for this method.
     */
    boolean isDefaultValue(Object value);

    /**
     * Returns a type token that represents this method's raw return and parameter types.
     *
     * @return A type token that represents this method's raw return and parameter types.
     */
    TypeToken asTypeToken();

    /**
     * Represents a method in its defined shape, i.e. in the form it is defined by a class without its type variables being resolved.
     */
    interface InDefinedShape extends MethodDescription, ByteCodeElement.Accessible {

        @Override
        TypeDescription getDeclaringType();

        @Override
        ParameterList<ParameterDescription.InDefinedShape> getParameters();

        /**
         * An abstract base implementation of a method description in its defined shape.
         */
        abstract class AbstractBase extends MethodDescription.AbstractBase implements InDefinedShape {

            @Override
            public InDefinedShape asDefined() {
                return this;
            }

            @Override
            public boolean isAccessibleTo(TypeDescription typeDescription) {
                return isVisibleTo(typeDescription) && getDeclaringType().isVisibleTo(typeDescription);
            }
        }
    }

    /**
     * An abstract base implementation of a method description.
     */
    abstract class AbstractBase extends ModifierReviewable.AbstractBase implements MethodDescription {

        /**
         * A merger of all method modifiers that are visible in the Java source code.
         */
        private static final int SOURCE_MODIFIERS = Modifier.PUBLIC
                | Modifier.PROTECTED
                | Modifier.PRIVATE
                | Modifier.ABSTRACT
                | Modifier.STATIC
                | Modifier.FINAL
                | Modifier.SYNCHRONIZED
                | Modifier.NATIVE;

        @Override
        public int getStackSize() {
            return getParameters().asTypeList().getStackSize() + (isStatic() ? 0 : 1);
        }

        @Override
        public boolean isMethod() {
            return !isConstructor() && !isTypeInitializer();
        }

        @Override
        public boolean isConstructor() {
            return CONSTRUCTOR_INTERNAL_NAME.equals(getInternalName());
        }

        @Override
        public boolean isTypeInitializer() {
            return TYPE_INITIALIZER_INTERNAL_NAME.equals(getInternalName());
        }

        @Override
        public boolean represents(Method method) {
            return equals(new ForLoadedMethod(method));
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return equals(new ForLoadedConstructor(constructor));
        }

        @Override
        public String getName() {
            return isMethod()
                    ? getInternalName()
                    : getDeclaringType().asErasure().getName();
        }

        @Override
        public String getSourceCodeName() {
            return isMethod()
                    ? getName()
                    : EMPTY_NAME;
        }

        @Override
        public String getDescriptor() {
            StringBuilder descriptor = new StringBuilder("(");
            for (TypeDescription parameterType : getParameters().asTypeList().asErasures()) {
                descriptor.append(parameterType.getDescriptor());
            }
            return descriptor.append(")").append(getReturnType().asErasure().getDescriptor()).toString();
        }

        @Override
        public String getGenericSignature() {
            try {
                SignatureWriter signatureWriter = new SignatureWriter();
                boolean generic = false;
                for (GenericTypeDescription typeVariable : getTypeVariables()) {
                    signatureWriter.visitFormalTypeParameter(typeVariable.getSymbol());
                    boolean classBound = true;
                    for (GenericTypeDescription upperBound : typeVariable.getUpperBounds()) {
                        upperBound.accept(new GenericTypeDescription.Visitor.ForSignatureVisitor(classBound
                                ? signatureWriter.visitClassBound()
                                : signatureWriter.visitInterfaceBound()));
                        classBound = false;
                    }
                    generic = true;
                }
                for (GenericTypeDescription parameterType : getParameters().asTypeList()) {
                    parameterType.accept(new GenericTypeDescription.Visitor.ForSignatureVisitor(signatureWriter.visitParameterType()));
                    generic = generic || !parameterType.getSort().isNonGeneric();
                }
                GenericTypeDescription returnType = getReturnType();
                returnType.accept(new GenericTypeDescription.Visitor.ForSignatureVisitor(signatureWriter.visitReturnType()));
                generic = generic || !returnType.getSort().isNonGeneric();
                GenericTypeList exceptionTypes = getExceptionTypes();
                if (!exceptionTypes.filter(not(ofSort(GenericTypeDescription.Sort.NON_GENERIC))).isEmpty()) {
                    for (GenericTypeDescription exceptionType : exceptionTypes) {
                        exceptionType.accept(new GenericTypeDescription.Visitor.ForSignatureVisitor(signatureWriter.visitExceptionType()));
                        generic = generic || !exceptionType.getSort().isNonGeneric();
                    }
                }
                return generic
                        ? signatureWriter.toString()
                        : NON_GENERIC_SIGNATURE;
            } catch (GenericSignatureFormatError ignored) {
                return NON_GENERIC_SIGNATURE;
            }
        }

        @Override
        public int getAdjustedModifiers(boolean nonAbstract) {
            return nonAbstract
                    ? getModifiers() & ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)
                    : getModifiers() & ~Opcodes.ACC_NATIVE | Opcodes.ACC_ABSTRACT;
        }

        @Override
        public boolean isVisibleTo(TypeDescription typeDescription) {
            return (isVirtual() || getDeclaringType().asErasure().isVisibleTo(typeDescription))
                    && (isPublic()
                    || typeDescription.equals(getDeclaringType())
                    || (isProtected() && getDeclaringType().asErasure().isAssignableFrom(typeDescription))
                    || (!isPrivate() && typeDescription.isSamePackage(getDeclaringType().asErasure())));
        }

        @Override
        public boolean isVirtual() {
            return !(isConstructor() || isPrivate() || isStatic() || isTypeInitializer());
        }

        @Override
        public boolean isDefaultMethod() {
            return !isAbstract() && !isBridge() && getDeclaringType().asErasure().isInterface();
        }

        @Override
        public boolean isSpecializableFor(TypeDescription targetType) {
            if (isStatic()) { // Static private methods are never specializable, check static property first
                return false;
            } else if (isPrivate() || isConstructor() || isDefaultMethod()) {
                return getDeclaringType().equals(targetType);
            } else {
                return !isAbstract() && getDeclaringType().asErasure().isAssignableFrom(targetType);
            }
        }

        @Override
        public <T> T getDefaultValue(Class<T> type) {
            return type.cast(getDefaultValue());
        }

        @Override
        public boolean isInvokableOn(TypeDescription typeDescription) {
            return !isStatic()
                    && !isTypeInitializer()
                    && isVisibleTo(typeDescription)
                    && (isVirtual()
                    ? getDeclaringType().asErasure().isAssignableFrom(typeDescription)
                    : getDeclaringType().asErasure().equals(typeDescription));
        }

        @Override
        public boolean isBootstrap() {
            TypeDescription returnType = getReturnType().asErasure();
            if ((isMethod() && (!isStatic()
                    || !(JavaType.CALL_SITE.getTypeStub().isAssignableFrom(returnType) || JavaType.CALL_SITE.getTypeStub().isAssignableTo(returnType))))
                    || (isConstructor() && !JavaType.CALL_SITE.getTypeStub().isAssignableFrom(getDeclaringType().asErasure()))) {
                return false;
            }
            TypeList parameterTypes = getParameters().asTypeList().asErasures();
            switch (parameterTypes.size()) {
                case 0:
                    return false;
                case 1:
                    return parameterTypes.getOnly().represents(Object[].class);
                case 2:
                    return JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo(parameterTypes.get(0))
                            && parameterTypes.get(1).represents(Object[].class);
                case 3:
                    return JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo(parameterTypes.get(0))
                            && (parameterTypes.get(1).represents(Object.class) || parameterTypes.get(1).represents(String.class))
                            && (parameterTypes.get(2).represents(Object[].class) || JavaType.METHOD_TYPE.getTypeStub().isAssignableTo(parameterTypes.get(2)));
                default:
                    if (!(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub().isAssignableTo(parameterTypes.get(0))
                            && (parameterTypes.get(1).represents(Object.class) || parameterTypes.get(1).represents(String.class))
                            && (JavaType.METHOD_TYPE.getTypeStub().isAssignableTo(parameterTypes.get(2))))) {
                        return false;
                    }
                    int parameterIndex = 4;
                    for (TypeDescription parameterType : parameterTypes.subList(3, parameterTypes.size())) {
                        if (!parameterType.represents(Object.class) && !parameterType.isConstantPool()) {
                            return parameterType.represents(Object[].class) && parameterIndex == parameterTypes.size();
                        }
                        parameterIndex++;
                    }
                    return true;
            }
        }

        @Override
        public boolean isBootstrap(List<?> arguments) {
            if (!isBootstrap()) {
                return false;
            }
            for (Object argument : arguments) {
                Class<?> argumentType = argument.getClass();
                if (!(argumentType == String.class
                        || argumentType == Integer.class
                        || argumentType == Long.class
                        || argumentType == Float.class
                        || argumentType == Double.class
                        || TypeDescription.class.isAssignableFrom(argumentType)
                        || JavaInstance.MethodHandle.class.isAssignableFrom(argumentType)
                        || JavaInstance.MethodType.class.isAssignableFrom(argumentType))) {
                    throw new IllegalArgumentException("Not a bootstrap argument: " + argument);
                }
            }
            TypeList parameterTypes = getParameters().asTypeList().asErasures();
            // The following assumes that the bootstrap method is a valid one, as checked above.
            if (parameterTypes.size() < 4) {
                return arguments.isEmpty() || parameterTypes.get(parameterTypes.size() - 1).represents(Object[].class);
            } else {
                int index = 4;
                Iterator<?> argumentIterator = arguments.iterator();
                for (TypeDescription parameterType : parameterTypes.subList(3, parameterTypes.size())) {
                    boolean finalParameterCheck = !argumentIterator.hasNext();
                    if (!finalParameterCheck) {
                        Class<?> argumentType = argumentIterator.next().getClass();
                        finalParameterCheck = !(parameterType.represents(String.class) && argumentType == String.class)
                                && !(parameterType.represents(int.class) && argumentType == Integer.class)
                                && !(parameterType.represents(long.class) && argumentType == Long.class)
                                && !(parameterType.represents(float.class) && argumentType == Float.class)
                                && !(parameterType.represents(double.class) && argumentType == Double.class)
                                && !(parameterType.represents(Class.class) && TypeDescription.class.isAssignableFrom(argumentType))
                                && !(parameterType.isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub()) && JavaInstance.MethodHandle.class.isAssignableFrom(argumentType))
                                && !(parameterType.equals(JavaType.METHOD_TYPE.getTypeStub()) && JavaInstance.MethodType.class.isAssignableFrom(argumentType));
                    }
                    if (finalParameterCheck) {
                        return index == parameterTypes.size() && parameterType.represents(Object[].class);
                    }
                    index++;
                }
                return true;
            }
        }

        @Override
        public boolean isDefaultValue() {
            return !isConstructor()
                    && !isStatic()
                    && getReturnType().asErasure().isAnnotationReturnType()
                    && getParameters().isEmpty();
        }

        @Override
        public boolean isDefaultValue(Object value) {
            if (!isDefaultValue()) {
                return false;
            }
            TypeDescription returnType = getReturnType().asErasure();
            return (returnType.represents(boolean.class) && value instanceof Boolean)
                    || (returnType.represents(byte.class) && value instanceof Byte)
                    || (returnType.represents(char.class) && value instanceof Character)
                    || (returnType.represents(short.class) && value instanceof Short)
                    || (returnType.represents(int.class) && value instanceof Integer)
                    || (returnType.represents(long.class) && value instanceof Long)
                    || (returnType.represents(float.class) && value instanceof Float)
                    || (returnType.represents(long.class) && value instanceof Long)
                    || (returnType.represents(String.class) && value instanceof String)
                    || (returnType.isAssignableTo(Enum.class) && value instanceof EnumerationDescription)
                    || (returnType.isAssignableTo(Annotation.class) && value instanceof AnnotationDescription)
                    || (returnType.represents(Class.class) && value instanceof TypeDescription);
        }

        @Override
        public TypeVariableSource getEnclosingSource() {
            return getDeclaringType().asErasure();
        }

        @Override
        public GenericTypeDescription findVariable(String symbol) {
            GenericTypeList typeVariables = getTypeVariables().filter(named(symbol));
            return typeVariables.isEmpty()
                    ? getEnclosingSource().findVariable(symbol)
                    : typeVariables.getOnly();
        }

        @Override
        public <T> T accept(TypeVariableSource.Visitor<T> visitor) {
            return visitor.onMethod(this);
        }

        @Override
        public Token asToken() {
            return asToken(none());
        }

        @Override
        public Token asToken(ElementMatcher<? super GenericTypeDescription> targetTypeMatcher) {
            GenericTypeDescription.Visitor<GenericTypeDescription> visitor = new GenericTypeDescription.Visitor.Substitutor.ForDetachment(targetTypeMatcher);
            return new Token(getInternalName(),
                    getModifiers(),
                    getTypeVariables().accept(visitor),
                    getReturnType().accept(visitor),
                    getParameters().asTokenList(targetTypeMatcher),
                    getExceptionTypes().accept(visitor),
                    getDeclaredAnnotations(),
                    getDefaultValue());
        }

        @Override
        public TypeToken asTypeToken() {
            return new TypeToken(getReturnType().asErasure(), getParameters().asTypeList().asErasures());
        }

        @Override
        public boolean equals(Object other) {
            return other == this || other instanceof MethodDescription
                    && getInternalName().equals(((MethodDescription) other).getInternalName())
                    && getDeclaringType().equals(((MethodDescription) other).getDeclaringType())
                    && getReturnType().asErasure().equals(((MethodDescription) other).getReturnType().asErasure())
                    && getParameters().asTypeList().asErasures().equals(((MethodDescription) other).getParameters().asTypeList().asErasures());
        }

        @Override
        public int hashCode() {
            int hashCode = getDeclaringType().hashCode();
            hashCode = 31 * hashCode + getInternalName().hashCode();
            hashCode = 31 * hashCode + getReturnType().asErasure().hashCode();
            return 31 * hashCode + getParameters().asTypeList().asErasures().hashCode();
        }

        @Override
        public String toGenericString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = getModifiers() & SOURCE_MODIFIERS;
            if (modifiers != EMPTY_MASK) {
                stringBuilder.append(Modifier.toString(modifiers)).append(" ");
            }
            if (isMethod()) {
                stringBuilder.append(getReturnType().getSourceCodeName()).append(" ");
                stringBuilder.append(getDeclaringType().asErasure().getSourceCodeName()).append(".");
            }
            stringBuilder.append(getName()).append("(");
            boolean first = true;
            for (GenericTypeDescription typeDescription : getParameters().asTypeList()) {
                if (!first) {
                    stringBuilder.append(",");
                } else {
                    first = false;
                }
                stringBuilder.append(typeDescription.getSourceCodeName());
            }
            stringBuilder.append(")");
            GenericTypeList exceptionTypes = getExceptionTypes();
            if (!exceptionTypes.isEmpty()) {
                stringBuilder.append(" throws ");
                first = true;
                for (GenericTypeDescription typeDescription : exceptionTypes) {
                    if (!first) {
                        stringBuilder.append(",");
                    } else {
                        first = false;
                    }
                    stringBuilder.append(typeDescription.getSourceCodeName());
                }
            }
            return stringBuilder.toString();
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            int modifiers = getModifiers() & SOURCE_MODIFIERS;
            if (modifiers != EMPTY_MASK) {
                stringBuilder.append(Modifier.toString(modifiers)).append(" ");
            }
            if (isMethod()) {
                stringBuilder.append(getReturnType().asErasure().getSourceCodeName()).append(" ");
                stringBuilder.append(getDeclaringType().asErasure().getSourceCodeName()).append(".");
            }
            stringBuilder.append(getName()).append("(");
            boolean first = true;
            for (TypeDescription typeDescription : getParameters().asTypeList().asErasures()) {
                if (!first) {
                    stringBuilder.append(",");
                } else {
                    first = false;
                }
                stringBuilder.append(typeDescription.getSourceCodeName());
            }
            stringBuilder.append(")");
            TypeList exceptionTypes = getExceptionTypes().asErasures();
            if (!exceptionTypes.isEmpty()) {
                stringBuilder.append(" throws ");
                first = true;
                for (TypeDescription typeDescription : exceptionTypes) {
                    if (!first) {
                        stringBuilder.append(",");
                    } else {
                        first = false;
                    }
                    stringBuilder.append(typeDescription.getSourceCodeName());
                }
            }
            return stringBuilder.toString();
        }
    }

    /**
     * An implementation of a method description for a loaded constructor.
     */
    class ForLoadedConstructor extends InDefinedShape.AbstractBase {

        /**
         * The loaded constructor that is represented by this instance.
         */
        private final Constructor<?> constructor;

        /**
         * Creates a new immutable method description for a loaded constructor.
         *
         * @param constructor The loaded constructor to be represented by this method description.
         */
        public ForLoadedConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public TypeDescription getDeclaringType() {
            return new TypeDescription.ForLoadedType(constructor.getDeclaringClass());
        }

        @Override
        public GenericTypeDescription getReturnType() {
            return TypeDescription.VOID;
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            return ParameterList.ForLoadedExecutable.of(constructor);
        }

        @Override
        public GenericTypeList getExceptionTypes() {
            return new GenericTypeList.OfConstructorExceptionTypes(constructor);
        }

        @Override
        public boolean isConstructor() {
            return true;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean represents(Method method) {
            return false;
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return this.constructor.equals(constructor) || equals(new ForLoadedConstructor(constructor));
        }

        @Override
        public String getName() {
            return constructor.getName();
        }

        @Override
        public int getModifiers() {
            return constructor.getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return constructor.isSynthetic();
        }

        @Override
        public String getInternalName() {
            return CONSTRUCTOR_INTERNAL_NAME;
        }

        @Override
        public String getDescriptor() {
            return Type.getConstructorDescriptor(constructor);
        }

        @Override
        public Object getDefaultValue() {
            return NO_DEFAULT_VALUE;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotation(constructor.getDeclaredAnnotations());
        }

        @Override
        public GenericTypeList getTypeVariables() {
            return new GenericTypeList.ForLoadedType(constructor.getTypeParameters());
        }
    }

    /**
     * An implementation of a method description for a loaded method.
     */
    class ForLoadedMethod extends InDefinedShape.AbstractBase {

        /**
         * The loaded method that is represented by this instance.
         */
        private final Method method;

        /**
         * Creates a new immutable method description for a loaded method.
         *
         * @param method The loaded method to be represented by this method description.
         */
        public ForLoadedMethod(Method method) {
            this.method = method;
        }

        @Override
        public TypeDescription getDeclaringType() {
            return new TypeDescription.ForLoadedType(method.getDeclaringClass());
        }

        @Override
        public GenericTypeDescription getReturnType() {
            return new GenericTypeDescription.LazyProjection.OfLoadedReturnType(method);
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            return ParameterList.ForLoadedExecutable.of(method);
        }

        @Override
        public GenericTypeList getExceptionTypes() {
            return new GenericTypeList.OfMethodExceptionTypes(method);
        }

        @Override
        public boolean isConstructor() {
            return false;
        }

        @Override
        public boolean isTypeInitializer() {
            return false;
        }

        @Override
        public boolean isBridge() {
            return method.isBridge();
        }

        @Override
        public boolean represents(Method method) {
            return this.method.equals(method) || equals(new ForLoadedMethod(method));
        }

        @Override
        public boolean represents(Constructor<?> constructor) {
            return false;
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public int getModifiers() {
            return method.getModifiers();
        }

        @Override
        public boolean isSynthetic() {
            return method.isSynthetic();
        }

        @Override
        public String getInternalName() {
            return method.getName();
        }

        @Override
        public String getDescriptor() {
            return Type.getMethodDescriptor(method);
        }

        /**
         * Returns the loaded method that is represented by this method description.
         *
         * @return The loaded method that is represented by this method description.
         */
        public Method getLoadedMethod() {
            return method;
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.ForLoadedAnnotation(method.getDeclaredAnnotations());
        }

        @Override
        public Object getDefaultValue() {
            Object value = method.getDefaultValue();
            return value == null
                    ? NO_DEFAULT_VALUE
                    : AnnotationDescription.ForLoadedAnnotation.describe(value, new TypeDescription.ForLoadedType(method.getReturnType()));
        }

        @Override
        public GenericTypeList getTypeVariables() {
            return new GenericTypeList.ForLoadedType(method.getTypeParameters());
        }
    }

    /**
     * A latent method description describes a method that is not attached to a declaring
     * {@link TypeDescription}.
     */
    class Latent extends InDefinedShape.AbstractBase {

        /**
         * The type that is declaring this method.
         */
        private final TypeDescription declaringType;

        /**
         * The internal name of this method.
         */
        private final String internalName;

        /**
         * The modifiers of this method.
         */
        private final int modifiers;

        /**
         * The type variables of the described method.
         */
        private final List<? extends GenericTypeDescription> typeVariables;

        /**
         * The return type of this method.
         */
        private final GenericTypeDescription returnType;

        /**
         * The parameter tokens describing this method.
         */
        private final List<? extends ParameterDescription.Token> parameterTokens;

        /**
         * This method's exception types.
         */
        private final List<? extends GenericTypeDescription> exceptionTypes;

        /**
         * The annotations of this method.
         */
        private final List<? extends AnnotationDescription> declaredAnnotations;

        /**
         * The default value of this method or {@code null} if no default annotation value is defined.
         */
        private final Object defaultValue;

        /**
         * Creates a new latent method description. All provided types are attached to this instance before they are returned.
         *
         * @param declaringType The declaring type of the method.
         * @param token         A token representing the method's shape.
         */
        public Latent(TypeDescription declaringType, Token token) {
            this(declaringType,
                    token.getInternalName(),
                    token.getModifiers(),
                    token.getTypeVariables(),
                    token.getReturnType(),
                    token.getParameterTokens(),
                    token.getExceptionTypes(),
                    token.getAnnotations(),
                    token.getDefaultValue());
        }

        /**
         * Creates a new latent method description. All provided types are attached to this instance before they are returned.
         *
         * @param declaringType       The type that is declaring this method.
         * @param internalName        The internal name of this method.
         * @param modifiers           The modifiers of this method.
         * @param typeVariables       The type variables of the described method.
         * @param returnType          The return type of this method.
         * @param parameterTokens     The parameter tokens describing this method.
         * @param exceptionTypes      This method's exception types.
         * @param declaredAnnotations The annotations of this method.
         * @param defaultValue        The default value of this method or {@code null} if no default annotation value is defined.
         */
        public Latent(TypeDescription declaringType,
                      String internalName,
                      int modifiers,
                      List<? extends GenericTypeDescription> typeVariables,
                      GenericTypeDescription returnType,
                      List<? extends ParameterDescription.Token> parameterTokens,
                      List<? extends GenericTypeDescription> exceptionTypes,
                      List<? extends AnnotationDescription> declaredAnnotations,
                      Object defaultValue) {
            this.declaringType = declaringType;
            this.internalName = internalName;
            this.modifiers = modifiers;
            this.typeVariables = typeVariables;
            this.returnType = returnType;
            this.parameterTokens = parameterTokens;
            this.exceptionTypes = exceptionTypes;
            this.declaredAnnotations = declaredAnnotations;
            this.defaultValue = defaultValue;
        }

        @Override
        public GenericTypeList getTypeVariables() {
            return GenericTypeList.ForDetachedTypes.OfTypeVariable.attach(this, typeVariables);
        }

        @Override
        public GenericTypeDescription getReturnType() {
            return returnType.accept(GenericTypeDescription.Visitor.Substitutor.ForAttachment.of(this));
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
            return new ParameterList.ForTokens(this, parameterTokens);
        }

        @Override
        public GenericTypeList getExceptionTypes() {
            return GenericTypeList.ForDetachedTypes.attach(this, exceptionTypes);
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return new AnnotationList.Explicit(declaredAnnotations);
        }

        @Override
        public String getInternalName() {
            return internalName;
        }

        @Override
        public TypeDescription getDeclaringType() {
            return declaringType;
        }

        @Override
        public int getModifiers() {
            return modifiers;
        }

        @Override
        public Object getDefaultValue() {
            return defaultValue;
        }

        /**
         * A method description that represents the type initializer.
         */
        public static class TypeInitializer extends InDefinedShape.AbstractBase {

            /**
             * The type for which the type initializer should be represented.
             */
            private final TypeDescription typeDescription;

            /**
             * Creates a new method description representing the type initializer.
             *
             * @param typeDescription The type for which the type initializer should be represented.
             */
            public TypeInitializer(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            @Override
            public GenericTypeDescription getReturnType() {
                return TypeDescription.VOID;
            }

            @Override
            public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                return new ParameterList.Empty();
            }

            @Override
            public GenericTypeList getExceptionTypes() {
                return new GenericTypeList.Empty();
            }

            @Override
            public Object getDefaultValue() {
                return NO_DEFAULT_VALUE;
            }

            @Override
            public GenericTypeList getTypeVariables() {
                return new GenericTypeList.Empty();
            }

            @Override
            public AnnotationList getDeclaredAnnotations() {
                return new AnnotationList.Empty();
            }

            @Override
            public TypeDescription getDeclaringType() {
                return typeDescription;
            }

            @Override
            public int getModifiers() {
                return TYPE_INITIALIZER_MODIFIER;
            }

            @Override
            public String getInternalName() {
                return MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME;
            }
        }
    }

    /**
     * A method description that represents a given method but with substituted method types.
     */
    class TypeSubstituting extends AbstractBase {

        /**
         * The type that declares this type-substituted method.
         */
        private final GenericTypeDescription declaringType;

        /**
         * The represented method description.
         */
        private final MethodDescription methodDescription;

        /**
         * A visitor that is applied to the method type.
         */
        private final GenericTypeDescription.Visitor<? extends GenericTypeDescription> visitor;

        /**
         * Creates a method description with substituted method types.
         *
         * @param declaringType     The type that is declaring the substituted method.
         * @param methodDescription The represented method description.
         * @param visitor           A visitor that is applied to the method type.
         */
        public TypeSubstituting(GenericTypeDescription declaringType,
                                MethodDescription methodDescription,
                                GenericTypeDescription.Visitor<? extends GenericTypeDescription> visitor) {
            this.declaringType = declaringType;
            this.methodDescription = methodDescription;
            this.visitor = visitor;
        }

        @Override
        public GenericTypeList getTypeVariables() {
            return new GenericTypeList.ForDetachedTypes(methodDescription.getTypeVariables(), new VariableRetainingDelegator());
        }

        @Override
        public GenericTypeDescription getReturnType() {
            return methodDescription.getReturnType().accept(new VariableRetainingDelegator());
        }

        @Override
        public ParameterList<?> getParameters() {
            return new ParameterList.TypeSubstituting(this, methodDescription.getParameters(), new VariableRetainingDelegator());
        }

        @Override
        public GenericTypeList getExceptionTypes() {
            return new GenericTypeList.ForDetachedTypes(methodDescription.getExceptionTypes(), new VariableRetainingDelegator());
        }

        @Override
        public Object getDefaultValue() {
            return methodDescription.getDefaultValue();
        }

        @Override
        public AnnotationList getDeclaredAnnotations() {
            return methodDescription.getDeclaredAnnotations();
        }

        @Override
        public GenericTypeDescription getDeclaringType() {
            return declaringType;
        }

        @Override
        public int getModifiers() {
            return methodDescription.getModifiers();
        }

        @Override
        public String getInternalName() {
            return methodDescription.getInternalName();
        }

        @Override
        public InDefinedShape asDefined() {
            return methodDescription.asDefined();
        }

        /**
         * A visitor that only escalates to the actual visitor if a non-generic type is discovered or if a type variable
         * that is not declared by the represented method is discovered. This way, a method's type variables are never bound
         * by the supplied visitor as non-generic types never reference a method's type variables and since a type variable
         * that is not declared by the represented method can never reference a type variable of the represented method.
         */
        protected class VariableRetainingDelegator extends GenericTypeDescription.Visitor.Substitutor {

            @Override
            public GenericTypeDescription onParameterizedType(GenericTypeDescription parameterizedType) {
                List<GenericTypeDescription> parameters = new ArrayList<GenericTypeDescription>(parameterizedType.getParameters().size());
                for (GenericTypeDescription parameter : parameterizedType.getParameters()) {
                    if (parameter.getSort().isTypeVariable() && !methodDescription.getTypeVariables().contains(parameter)) {
                        return visitor.onParameterizedType(parameterizedType);
                    } else if (parameter.getSort().isWildcard()) {
                        GenericTypeList bounds = parameter.getLowerBounds();
                        bounds = bounds.isEmpty() ? parameter.getUpperBounds() : bounds;
                        if (bounds.getOnly().getSort().isTypeVariable() && !methodDescription.getTypeVariables().contains(parameter)) {
                            return visitor.onParameterizedType(parameterizedType);
                        }
                    }
                    parameters.add(parameter.accept(this));
                }
                GenericTypeDescription ownerType = parameterizedType.getOwnerType();
                return new GenericTypeDescription.ForParameterizedType.Latent(parameterizedType.asErasure(),
                        parameters,
                        ownerType == null
                                ? TypeDescription.UNDEFINED
                                : ownerType.accept(this));
            }

            @Override
            public GenericTypeDescription onNonGenericType(GenericTypeDescription typeDescription) {
                return visitor.onNonGenericType(typeDescription);
            }

            @Override
            protected GenericTypeDescription onSimpleType(GenericTypeDescription typeDescription) {
                throw new UnsupportedOperationException();
            }

            @Override
            public GenericTypeDescription onTypeVariable(GenericTypeDescription typeVariable) {
                return methodDescription.getTypeVariables().contains(typeVariable)
                        ? new RetainedVariable(typeVariable)
                        : visitor.onTypeVariable(typeVariable);
            }

            @Override
            public int hashCode() {
                return TypeSubstituting.this.hashCode();
            }

            @Override
            public boolean equals(Object other) {
                return other != null && other.getClass() == this.getClass()
                        && TypeSubstituting.this.equals(((VariableRetainingDelegator) other).getOuter());
            }

            /**
             * Returns the outer instance.
             *
             * @return The outer instance.
             */
            private Object getOuter() {
                return TypeSubstituting.this;
            }

            @Override
            public String toString() {
                return "MethodDescription.TypeSubstituting.VariableRetainingDelegator{methodDescription=" + TypeSubstituting.this + '}';
            }

            /**
             * A retained type variable that is declared by the method.
             */
            protected class RetainedVariable extends GenericTypeDescription.ForTypeVariable {

                /**
                 * The type variable this retained variable represents.
                 */
                private final GenericTypeDescription typeVariable;

                /**
                 * Creates a new retained type variable.
                 *
                 * @param typeVariable The type variable this retained variable represents.
                 */
                protected RetainedVariable(GenericTypeDescription typeVariable) {
                    this.typeVariable = typeVariable;
                }

                @Override
                public GenericTypeList getUpperBounds() {
                    return new GenericTypeList.ForDetachedTypes(typeVariable.getUpperBounds(), VariableRetainingDelegator.this);
                }

                @Override
                public TypeVariableSource getVariableSource() {
                    return TypeSubstituting.this;
                }

                @Override
                public String getSymbol() {
                    return typeVariable.getSymbol();
                }
            }
        }
    }

    /**
     * A token that represents a method's shape. A method token is equal to another token when the name, the raw return type
     * and the raw parameter types are equal to those of another method token.
     */
    class Token implements ByteCodeElement.Token<Token> {

        /**
         * The internal name of the represented method.
         */
        private final String internalName;

        /**
         * The modifiers of the represented method.
         */
        private final int modifiers;

        /**
         * The type variables of the the represented method.
         */
        private final List<GenericTypeDescription> typeVariables;

        /**
         * The return type of the represented method.
         */
        private final GenericTypeDescription returnType;

        /**
         * The parameter tokens of the represented method.
         */
        private final List<? extends ParameterDescription.Token> parameterTokens;

        /**
         * The exception types of the represented method.
         */
        private final List<? extends GenericTypeDescription> exceptionTypes;

        /**
         * The annotations of the represented method.
         */
        private final List<? extends AnnotationDescription> annotations;

        /**
         * The default value of the represented method or {@code null} if no such value exists.
         */
        private final Object defaultValue;

        /**
         * Creates a new method token with simple values.
         *
         * @param internalName   The internal name of the represented method.
         * @param modifiers      The modifiers of the represented method.
         * @param returnType     The return type of the represented method.
         * @param parameterTypes The parameter types of this method.
         */
        public Token(String internalName, int modifiers, GenericTypeDescription returnType, List<? extends GenericTypeDescription> parameterTypes) {
            this(internalName,
                    modifiers,
                    Collections.<GenericTypeDescription>emptyList(),
                    returnType,
                    new ParameterDescription.Token.TypeList(parameterTypes),
                    Collections.<GenericTypeDescription>emptyList(),
                    Collections.<AnnotationDescription>emptyList(),
                    NO_DEFAULT_VALUE);
        }

        /**
         * Creates a new token for a method description.
         *
         * @param internalName    The internal name of the represented method.
         * @param modifiers       The modifiers of the represented method.
         * @param typeVariables   The type variables of the the represented method.
         * @param returnType      The return type of the represented method.
         * @param parameterTokens The parameter tokens of the represented method.
         * @param exceptionTypes  The exception types of the represented method.
         * @param annotations     The annotations of the represented method.
         * @param defaultValue    The default value of the represented method or {@code null} if no such value exists.
         */
        public Token(String internalName,
                     int modifiers,
                     List<GenericTypeDescription> typeVariables,
                     GenericTypeDescription returnType,
                     List<? extends ParameterDescription.Token> parameterTokens,
                     List<? extends GenericTypeDescription> exceptionTypes,
                     List<? extends AnnotationDescription> annotations,
                     Object defaultValue) {
            this.internalName = internalName;
            this.modifiers = modifiers;
            this.typeVariables = typeVariables;
            this.returnType = returnType;
            this.parameterTokens = parameterTokens;
            this.exceptionTypes = exceptionTypes;
            this.annotations = annotations;
            this.defaultValue = defaultValue;
        }

        /**
         * Returns the internal name of the represented method.
         *
         * @return The internal name of the represented method.
         */
        public String getInternalName() {
            return internalName;
        }

        /**
         * Returns the modifiers of the represented method.
         *
         * @return The modifiers of the represented method.
         */
        public int getModifiers() {
            return modifiers;
        }

        /**
         * Returns the type variables of the the represented method.
         *
         * @return The type variables of the the represented method.
         */
        public GenericTypeList getTypeVariables() {
            return new GenericTypeList.Explicit(typeVariables);
        }

        /**
         * Returns the return type of the represented method.
         *
         * @return The return type of the represented method.
         */
        public GenericTypeDescription getReturnType() {
            return returnType;
        }

        /**
         * Returns the parameter tokens of the represented method.
         *
         * @return The parameter tokens of the represented method.
         */
        public TokenList<ParameterDescription.Token> getParameterTokens() {
            return new TokenList<ParameterDescription.Token>(parameterTokens);
        }

        /**
         * Returns the exception types of the represented method.
         *
         * @return The exception types of the represented method.
         */
        public GenericTypeList getExceptionTypes() {
            return new GenericTypeList.Explicit(exceptionTypes);
        }

        /**
         * Returns the annotations of the represented method.
         *
         * @return The annotations of the represented method.
         */
        public AnnotationList getAnnotations() {
            return new AnnotationList.Explicit(annotations);
        }

        /**
         * Returns the default value of the represented method.
         *
         * @return The default value of the represented method or {@code null} if no such value exists.
         */
        public Object getDefaultValue() {
            return defaultValue;
        }

        /**
         * Transforms this method token into a type token.
         *
         * @return A type token representing the type's of this method token.
         */
        public TypeToken asTypeToken() {
            List<TypeDescription> parameterTypes = new ArrayList<TypeDescription>(getParameterTokens().size());
            for (ParameterDescription.Token parameterToken : getParameterTokens()) {
                parameterTypes.add(parameterToken.getType().asErasure());
            }
            return new TypeToken(getReturnType().asErasure(), parameterTypes);
        }

        @Override
        public Token accept(GenericTypeDescription.Visitor<? extends GenericTypeDescription> visitor) {
            return new Token(getInternalName(),
                    getModifiers(),
                    getTypeVariables().accept(visitor),
                    getReturnType().accept(visitor),
                    getParameterTokens().accept(visitor),
                    getExceptionTypes().accept(visitor),
                    getAnnotations(),
                    getDefaultValue());
        }

        @Override
        public boolean isIdenticalTo(Token token) {
            return getInternalName().equals(token.getInternalName())
                    && getModifiers() == token.getModifiers()
                    && getTypeVariables().equals(token.getTypeVariables())
                    && getReturnType().equals(token.getReturnType())
                    && getParameterTokens().equals(token.getParameterTokens())
                    && getExceptionTypes().equals(token.getExceptionTypes())
                    && getAnnotations().equals(token.getAnnotations())
                    && ((getDefaultValue() == null && token.getDefaultValue() == null)
                    || (getDefaultValue() != null && token.getDefaultValue() != null && (getDefaultValue().equals(token.getDefaultValue()))));
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (!(other instanceof Token)) return false;
            Token token = (Token) other;
            if (!getInternalName().equals(token.getInternalName())) return false;
            if (!getReturnType().asErasure().equals(token.getReturnType().asErasure())) return false;
            List<ParameterDescription.Token> tokens = getParameterTokens(), otherTokens = token.getParameterTokens();
            if (tokens.size() != otherTokens.size()) return false;
            for (int index = 0; index < tokens.size(); index++) {
                if (!tokens.get(index).getType().asErasure().equals(otherTokens.get(index).getType().asErasure())) return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = getInternalName().hashCode();
            result = 31 * result + getReturnType().asErasure().hashCode();
            for (ParameterDescription.Token parameterToken : getParameterTokens()) {
                result = 31 * result + parameterToken.getType().asErasure().hashCode();
            }
            return result;
        }

        @Override
        public String toString() {
            return "MethodDescription.Token{" +
                    "internalName='" + internalName + '\'' +
                    ", modifiers=" + modifiers +
                    ", typeVariables=" + typeVariables +
                    ", returnType=" + returnType +
                    ", parameterTokens=" + parameterTokens +
                    ", exceptionTypes=" + exceptionTypes +
                    ", annotations=" + annotations +
                    ", defaultValue=" + defaultValue +
                    '}';
        }
    }

    /**
     * A token representing a method's erased return and parameter types.
     */
    class TypeToken {

        /**
         * The represented method's raw return type.
         */
        private final TypeDescription returnType;

        /**
         * The represented method's raw parameter types.
         */
        private final List<? extends TypeDescription> parameterTypes;

        /**
         * Creates a new type token.
         *
         * @param returnType     The represented method's raw return type.
         * @param parameterTypes The represented method's raw parameter types.
         */
        public TypeToken(TypeDescription returnType, List<? extends TypeDescription> parameterTypes) {
            this.returnType = returnType;
            this.parameterTypes = parameterTypes;
        }

        /**
         * Returns this token's return type.
         *
         * @return This token's return type.
         */
        public TypeDescription getReturnType() {
            return returnType;
        }

        /**
         * Returns this token's parameter types.
         *
         * @return This token's parameter types.
         */
        public List<TypeDescription> getParameterTypes() {
            return new ArrayList<TypeDescription>(parameterTypes);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            TypeToken typeToken = (TypeToken) other;
            return returnType.equals(typeToken.returnType)
                    && parameterTypes.equals(typeToken.parameterTypes);
        }

        @Override
        public int hashCode() {
            int result = returnType.hashCode();
            result = 31 * result + parameterTypes.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "MethodDescription.TypeToken{" +
                    "returnType=" + returnType +
                    ", parameterTypes=" + parameterTypes +
                    '}';
        }
    }
}
