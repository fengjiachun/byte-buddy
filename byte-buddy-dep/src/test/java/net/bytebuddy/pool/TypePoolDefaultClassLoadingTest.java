package net.bytebuddy.pool;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TypePoolDefaultClassLoadingTest {

    private final TypePool typePool;

    public TypePoolDefaultClassLoadingTest(TypePool typePool) {
        this.typePool = typePool;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {TypePool.Default.ClassLoading.of((ClassLoader) null)},
                {TypePool.Default.ClassLoading.of(ClassFileLocator.ForClassLoader.of(null), null)},
        });
    }

    @Test
    public void testLoadableBootstrapLoaderClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        assertThat(resolution.isResolved(), is(true));
        assertThat(resolution.resolve(), is(TypeDescription.OBJECT));
    }

    @Test
    public void testArrayClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object[].class.getName());
        assertThat(resolution.isResolved(), is(true));
        assertThat(resolution.resolve(), is((TypeDescription) new TypeDescription.ForLoadedType(Object[].class)));
    }

    @Test
    public void testPrimitiveClass() throws Exception {
        TypePool.Resolution resolution = typePool.describe(int.class.getName());
        assertThat(resolution.isResolved(), is(true));
        assertThat(resolution.resolve(), is((TypeDescription) new TypeDescription.ForLoadedType(int.class)));
    }

    @Test
    public void testClearRetainsFunctionality() throws Exception {
        TypePool.Resolution resolution = typePool.describe(Object.class.getName());
        assertThat(resolution.isResolved(), is(true));
        assertThat(resolution.resolve(), is(TypeDescription.OBJECT));
        typePool.clear();
        TypePool.Resolution otherResolution = typePool.describe(Object.class.getName());
        assertThat(otherResolution.isResolved(), is(true));
        assertThat(resolution.resolve(), is(TypeDescription.OBJECT));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(TypePool.Default.ClassLoading.class).apply();
    }
}
