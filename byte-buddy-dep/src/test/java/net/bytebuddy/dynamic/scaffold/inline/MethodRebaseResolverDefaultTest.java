package net.bytebuddy.dynamic.scaffold.inline;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.Opcodes;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

public class MethodRebaseResolverDefaultTest {

    private static final String FOO = "foo", BAR = "bar", QUX = "qux";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription.InDefinedShape methodDescription, otherMethod;

    @Mock
    private MethodRebaseResolver.Resolution resolution;

    @Mock
    private DynamicType dynamicType;

    @Mock
    private TypeDescription instrumentedType;

    @Mock
    private ClassFileVersion classFileVersion;

    @Mock
    private AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy;

    @Mock
    private MethodRebaseResolver.MethodNameTransformer methodNameTransformer;

    @Before
    public void setUp() throws Exception {
        when(methodDescription.asDefined()).thenReturn(methodDescription);
        when(methodDescription.getParameters()).thenReturn(new ParameterList.Empty());
        when(methodDescription.getReturnType()).thenReturn(TypeDescription.VOID);
        when(methodDescription.getInternalName()).thenReturn(FOO);
        when(methodNameTransformer.transform(methodDescription)).thenReturn(BAR);
        when(auxiliaryTypeNamingStrategy.name(instrumentedType)).thenReturn(QUX);
        when(classFileVersion.getMinorMajorVersion()).thenReturn(Opcodes.V1_6);
    }

    @Test
    public void testResolutionLookup() throws Exception {
        MethodRebaseResolver methodRebaseResolver = new MethodRebaseResolver.Default(Collections.singletonMap(methodDescription, resolution),
                Collections.singletonList(dynamicType));
        assertThat(methodRebaseResolver.resolve(methodDescription), is(resolution));
        assertThat(methodRebaseResolver.resolve(otherMethod).isRebased(), is(false));
        assertThat(methodRebaseResolver.resolve(otherMethod).getResolvedMethod(), is(otherMethod));
    }

    @Test
    public void testAuxiliaryTypes() throws Exception {
        MethodRebaseResolver methodRebaseResolver = new MethodRebaseResolver.Default(Collections.singletonMap(methodDescription, resolution),
                Collections.singletonList(dynamicType));
        assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), is(1));
        assertThat(methodRebaseResolver.getAuxiliaryTypes().contains(dynamicType), is(true));
    }

    @Test
    public void testCreationWithoutConstructor() throws Exception {
        MethodRebaseResolver methodRebaseResolver = MethodRebaseResolver.Default.make(instrumentedType,
                new MethodList.Explicit<MethodDescription.InDefinedShape>(Collections.singletonList(methodDescription)),
                classFileVersion,
                auxiliaryTypeNamingStrategy,
                methodNameTransformer);
        assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), is(0));
        MethodRebaseResolver.Resolution resolution = methodRebaseResolver.resolve(methodDescription);
        assertThat(resolution.isRebased(), is(true));
        assertThat(resolution.getResolvedMethod(), not(methodDescription));
        assertThat(resolution.getResolvedMethod().isConstructor(), is(false));
    }

    @Test
    public void testCreationWithConstructor() throws Exception {
        when(methodDescription.isConstructor()).thenReturn(true);
        MethodRebaseResolver methodRebaseResolver = MethodRebaseResolver.Default.make(instrumentedType,
                new MethodList.Explicit<MethodDescription.InDefinedShape>(Collections.singletonList(methodDescription)),
                classFileVersion,
                auxiliaryTypeNamingStrategy,
                methodNameTransformer);
        assertThat(methodRebaseResolver.getAuxiliaryTypes().size(), is(1));
        assertThat(methodRebaseResolver.getAuxiliaryTypes().get(0).getTypeDescription().getName(), is(QUX));
        MethodRebaseResolver.Resolution resolution = methodRebaseResolver.resolve(methodDescription);
        assertThat(resolution.isRebased(), is(true));
        assertThat(resolution.getResolvedMethod(), not(methodDescription));
        assertThat(resolution.getResolvedMethod().isConstructor(), is(true));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(MethodRebaseResolver.Default.class).apply();
    }
}
