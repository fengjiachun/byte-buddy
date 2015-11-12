package net.bytebuddy.implementation;

import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.TypeWriter;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImplementationContextDefaultOtherTest {

    @Test
    public void testFactory() throws Exception {
        assertThat(Implementation.Context.Default.Factory.INSTANCE.make(mock(TypeDescription.class),
                mock(AuxiliaryType.NamingStrategy.class),
                mock(InstrumentedType.TypeInitializer.class),
                mock(ClassFileVersion.class)), instanceOf(Implementation.Context.Default.class));
    }

    @Test
    public void testTypeInitializerNotRetained() throws Exception {
        assertThat(new Implementation.Context.Default(mock(TypeDescription.class),
                mock(AuxiliaryType.NamingStrategy.class),
                mock(InstrumentedType.TypeInitializer.class),
                mock(ClassFileVersion.class)).isRetainTypeInitializer(), is(false));
    }

    @Test
    public void testFrozenTypeInitializerRetainsInitializer() throws Exception {
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(mock(TypeDescription.class),
                mock(AuxiliaryType.NamingStrategy.class),
                mock(InstrumentedType.TypeInitializer.class),
                mock(ClassFileVersion.class));
        implementationContext.prohibitTypeInitializer();
        assertThat(implementationContext.isRetainTypeInitializer(), is(true));
    }

    @Test(expected = IllegalStateException.class)
    public void testFrozenTypeInitializerFrozenThrowsExceptionOnDrain() throws Exception {
        TypeDescription instrumentedType = mock(TypeDescription.class);
        Implementation.Context.ExtractableView implementationContext = new Implementation.Context.Default(instrumentedType,
                mock(AuxiliaryType.NamingStrategy.class),
                mock(InstrumentedType.TypeInitializer.class),
                mock(ClassFileVersion.class));
        implementationContext.prohibitTypeInitializer();
        TypeWriter.MethodPool methodPool = mock(TypeWriter.MethodPool.class);
        TypeWriter.MethodPool.Record record = mock(TypeWriter.MethodPool.Record.class);
        when(record.getSort()).thenReturn(TypeWriter.MethodPool.Record.Sort.DEFINED);
        when(methodPool.target(new MethodDescription.Latent.TypeInitializer(instrumentedType))).thenReturn(record);
        implementationContext.drain(mock(ClassVisitor.class), methodPool, mock(Implementation.Context.ExtractableView.InjectedCode.class));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(Implementation.Context.Default.class).applyBasic();
        ObjectPropertyAssertion.of(Implementation.Context.Default.FieldCacheEntry.class).apply();
        ObjectPropertyAssertion.of(Implementation.Context.Default.AccessorMethodDelegation.class).apply();
        ObjectPropertyAssertion.of(Implementation.Context.Default.FieldSetterDelegation.class).apply();
        ObjectPropertyAssertion.of(Implementation.Context.Default.FieldGetterDelegation.class).apply();
        ObjectPropertyAssertion.of(Implementation.Context.Default.Factory.class).apply();
    }
}
