package net.bytebuddy.dynamic.scaffold;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TypeWriterDefaultFrameComputingClassWriterTest {

    private static final String FOO = "pkg/foo", BAR = "pkg/bar", QUX = "pkg/qux", BAZ = "pkg/baz", FOOBAR = "pkg/foobar";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypePool typePool;

    @Mock
    private TypeDescription leftType, rightType, superType;

    private TypeWriter.Default.FrameComputingClassWriter frameComputingClassWriter;

    @Before
    public void setUp() throws Exception {
        frameComputingClassWriter = new TypeWriter.Default.FrameComputingClassWriter(mock(ClassReader.class), 0, typePool);
        when(typePool.describe(FOO.replace('/', '.'))).thenReturn(new TypePool.Resolution.Simple(leftType));
        when(typePool.describe(BAR.replace('/', '.'))).thenReturn(new TypePool.Resolution.Simple(rightType));
        when(leftType.getInternalName()).thenReturn(QUX);
        when(rightType.getInternalName()).thenReturn(BAZ);
        when(leftType.getSuperType()).thenReturn(superType);
        when(superType.asErasure()).thenReturn(superType);
        when(superType.getInternalName()).thenReturn(FOOBAR);
    }

    @Test
    public void testFactory() throws Exception {
        assertThat(TypeWriter.Default.FrameComputingClassWriter.of(mock(ClassReader.class), 0, mock(ClassFileLocator.class)),
                not(instanceOf(TypeWriter.Default.FrameComputingClassWriter.class)));
        assertThat(TypeWriter.Default.FrameComputingClassWriter.of(mock(ClassReader.class), ClassWriter.COMPUTE_FRAMES, mock(ClassFileLocator.class)),
                instanceOf(TypeWriter.Default.FrameComputingClassWriter.class));
    }

    @Test
    public void testLeftIsAssignable() throws Exception {
        when(leftType.isAssignableFrom(rightType)).thenReturn(true);
        assertThat(frameComputingClassWriter.getCommonSuperClass(FOO, BAR), is(QUX));
    }

    @Test
    public void testRightIsAssignable() throws Exception {
        when(leftType.isAssignableTo(rightType)).thenReturn(true);
        assertThat(frameComputingClassWriter.getCommonSuperClass(FOO, BAR), is(BAZ));
    }

    @Test
    public void testLeftIsInterface() throws Exception {
        when(leftType.isInterface()).thenReturn(true);
        assertThat(frameComputingClassWriter.getCommonSuperClass(FOO, BAR), is(TypeDescription.OBJECT.getInternalName()));
    }

    @Test
    public void testRightIsInterface() throws Exception {
        when(rightType.isInterface()).thenReturn(true);
        assertThat(frameComputingClassWriter.getCommonSuperClass(FOO, BAR), is(TypeDescription.OBJECT.getInternalName()));
    }

    @Test
    public void testSuperTypeIteration() throws Exception {
        when(superType.isAssignableFrom(rightType)).thenReturn(true);
        assertThat(frameComputingClassWriter.getCommonSuperClass(FOO, BAR), is(FOOBAR));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(TypeWriter.Default.FrameComputingClassWriter.class).applyBasic();
    }
}