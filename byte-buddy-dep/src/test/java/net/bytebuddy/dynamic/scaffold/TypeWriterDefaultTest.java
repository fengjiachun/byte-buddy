package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.asm.TypeConstantAdjustment;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.test.utility.JavaVersionRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import net.bytebuddy.utility.JavaInstance;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Modifier;
import java.util.Collections;

import static net.bytebuddy.matcher.ElementMatchers.isTypeInitializer;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TypeWriterDefaultTest {

    private static final String FOO = "foo", BAR = "bar";

    private static final String LEGACY_INTERFACE = "net.bytebuddy.test.precompiled.LegacyInterface";

    private static final String JAVA_8_INTERFACE = "net.bytebuddy.test.precompiled.SingleDefaultMethodInterface";

    @Rule
    public MethodRule javaVersionRule = new JavaVersionRule();


    @Test(expected = IllegalStateException.class)
    public void testConstructorOnInterfaceAssertion() throws Exception {
        new ByteBuddy()
                .makeInterface()
                .defineConstructor(Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .intercept(SuperMethodCall.INSTANCE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testConstructorOnAnnotationAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineConstructor(Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .intercept(SuperMethodCall.INSTANCE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAbstractConstructorAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class, ConstructorStrategy.Default.NO_CONSTRUCTORS)
                .defineConstructor(Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticAbstractMethodAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList(), Ownership.STATIC)
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPrivateAbstractMethodAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList(), Visibility.PRIVATE)
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAbstractMethodOnNonAbstractClassAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList())
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicFieldOnInterfaceAssertion() throws Exception {
        new ByteBuddy()
                .makeInterface()
                .defineField(FOO, String.class, Ownership.STATIC)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicFieldOnAnnotationAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineField(FOO, String.class, Ownership.STATIC)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticFieldOnInterfaceAssertion() throws Exception {
        new ByteBuddy()
                .makeInterface()
                .defineField(FOO, String.class, Visibility.PUBLIC)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonStaticFieldOnAnnotationAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineField(FOO, String.class, Visibility.PUBLIC)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicMethodOnInterfaceAssertion() throws Exception {
        new ByteBuddy()
                .makeInterface()
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList())
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testNonPublicMethodOnAnnotationAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList())
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethodOnInterfaceAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V6)
                .makeInterface()
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC, Ownership.STATIC)
                .withoutCode()
                .make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testStaticMethodOnAnnotationAssertionJava8() throws Exception {
        new ByteBuddy()
                .makeInterface()
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC, Ownership.STATIC)
                .intercept(StubMethod.INSTANCE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testStaticMethodOnAnnotationAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V6)
                .makeAnnotation()
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC, Ownership.STATIC)
                .intercept(StubMethod.INSTANCE)
                .make();
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testStaticMethodOnInterfaceAssertionJava8() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC, Ownership.STATIC)
                .intercept(StubMethod.INSTANCE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationDefaultValueOnClassAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList())
                .withDefaultValue(BAR)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationDefaultValueOnInterfaceClassAssertion() throws Exception {
        new ByteBuddy()
                .subclass(Object.class)
                .modifiers(TypeManifestation.INTERFACE)
                .defineMethod(FOO, String.class, Collections.<Class<?>>emptyList())
                .withDefaultValue(BAR)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPropertyWithVoidReturnAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPropertyWithParametersAssertion() throws Exception {
        new ByteBuddy()
                .makeAnnotation()
                .defineMethod(FOO, String.class, Collections.<Class<?>>singletonList(Void.class), Visibility.PUBLIC)
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithModifiers() throws Exception {
        new ByteBuddy()
                .makePackage(FOO)
                .modifiers(Visibility.PRIVATE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithInterfaces() throws Exception {
        new ByteBuddy()
                .makePackage(FOO)
                .implement(Serializable.class)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithField() throws Exception {
        new ByteBuddy()
                .makePackage(FOO)
                .defineField(FOO, Void.class)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testPackageDescriptionWithMethod() throws Exception {
        new ByteBuddy()
                .makePackage(FOO)
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList())
                .withoutCode()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .makeAnnotation()
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnTypePreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .annotateType(AnnotationDescription.Builder.forType(Foo.class).make())
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnFieldPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .defineField(FOO, Void.class)
                .annotateField(AnnotationDescription.Builder.forType(Foo.class).make())
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotationOnMethodPreJava5TypeAssertion() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .defineMethod(FOO, void.class, Collections.<Class<?>>emptyList())
                .intercept(StubMethod.INSTANCE)
                .annotateMethod(AnnotationDescription.Builder.forType(Foo.class).make())
                .make();
    }

    @Test
    public void testTypeInitializerOnInterface() throws Exception {
        assertThat(new ByteBuddy()
                .makeInterface()
                .invokable(isTypeInitializer())
                .intercept(StubMethod.INSTANCE)
                .make(), notNullValue(DynamicType.class));
    }

    @Test
    public void testTypeInitializerOnAnnotation() throws Exception {
        assertThat(new ByteBuddy()
                .makeAnnotation()
                .invokable(isTypeInitializer())
                .intercept(StubMethod.INSTANCE)
                .make(), notNullValue(DynamicType.class));
    }

    @Test
    @JavaVersionRule.Enforce(8)
    public void testTypeInitializerOnRebasedModernInterface() throws Exception {
        assertThat(new ByteBuddy()
                .rebase(Class.forName(JAVA_8_INTERFACE))
                .invokable(isTypeInitializer())
                .intercept(StubMethod.INSTANCE)
                .make(), notNullValue(DynamicType.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeInitializerOnRebasedLegacyInterface() throws Exception {
        new ByteBuddy()
                .rebase(Class.forName(LEGACY_INTERFACE))
                .invokable(isTypeInitializer())
                .intercept(StubMethod.INSTANCE)
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeInLegacyConstantPool() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList())
                .intercept(FixedValue.value(Object.class))
                .make();
    }

    @Test
    public void testTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4)
                .withClassVisitor(TypeConstantAdjustment.INSTANCE)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .intercept(FixedValue.value(Object.class))
                .make()
                .load(null, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        assertThat(dynamicType.getDeclaredMethod(FOO).invoke(dynamicType.newInstance()), is((Object) Object.class));
    }

    @Test
    public void testArrayTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4)
                .withClassVisitor(TypeConstantAdjustment.INSTANCE)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .intercept(FixedValue.value(Object[].class))
                .make()
                .load(null, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        assertThat(dynamicType.getDeclaredMethod(FOO).invoke(dynamicType.newInstance()), is((Object) Object[].class));
    }

    @Test
    public void testPrimitiveTypeInLegacyConstantPoolRemapped() throws Exception {
        Class<?> dynamicType = new ByteBuddy(ClassFileVersion.JAVA_V4)
                .withClassVisitor(TypeConstantAdjustment.INSTANCE)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList(), Visibility.PUBLIC)
                .intercept(FixedValue.value(int.class))
                .make()
                .load(null, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        assertThat(dynamicType.getDeclaredMethod(FOO).invoke(dynamicType.newInstance()), is((Object) int.class));
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodTypeInLegacyConstantPool() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList())
                .intercept(FixedValue.value(JavaInstance.MethodType.of(Object.class, Object.class)))
                .make();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethodHandleInLegacyConstantPool() throws Exception {
        new ByteBuddy(ClassFileVersion.JAVA_V4)
                .subclass(Object.class)
                .defineMethod(FOO, Object.class, Collections.<Class<?>>emptyList())
                .intercept(FixedValue.value(JavaInstance.MethodHandle.of(new MethodDescription.ForLoadedMethod(Object.class.getDeclaredMethod("toString")))))
                .make();
    }

    @Test
    public void testInnerClassChangeModifierTest() throws Exception {
        assertThat(new ByteBuddy()
                .redefine(Bar.class)
                .modifiers(Visibility.PUBLIC)
                .make()
                .load(null, ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .getModifiers(), is(Modifier.PUBLIC));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(TypeWriter.Default.ForCreation.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ForInlining.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ForInlining.FramePreservingRemapper.class).applyBasic();
        ObjectPropertyAssertion.of(TypeWriter.Default.ForInlining.FramePreservingRemapper.FramePreservingMethodRemapper.class)
                .create(new ObjectPropertyAssertion.Creator<String>() {
                    @Override
                    public String create() {
                        return "()V";
                    }
                }).applyBasic();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.class).applyBasic();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.ValidatingFieldVisitor.class).applyBasic();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.ValidatingMethodVisitor.class).applyBasic();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.ForAnnotation.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.ForInterface.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.ForClass.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.ForPackageType.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.ForClassFileVersion.class).apply();
        ObjectPropertyAssertion.of(TypeWriter.Default.ValidatingClassVisitor.Constraint.Compound.class).apply();
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Foo {
        /* empty */
    }

    class Bar {
        /* empty */
    }
}
