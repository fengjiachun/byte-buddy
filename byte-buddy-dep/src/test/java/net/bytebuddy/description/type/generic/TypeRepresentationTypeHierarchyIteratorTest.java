package net.bytebuddy.description.type.generic;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeRepresentation;
import net.bytebuddy.test.utility.MockitoRule;
import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

public class TypeRepresentationTypeHierarchyIteratorTest {

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeRepresentation typeDescription;

    @Mock
    private GenericTypeDescription superType;

    @Before
    public void setUp() throws Exception {
        when(typeDescription.getSuperType()).thenReturn(superType);
    }

    @Test
    public void testHasNext() throws Exception {
        Iterator<TypeRepresentation> iterator = new TypeRepresentation.TypeHierarchyIterator(typeDescription);
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(typeDescription));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is((TypeRepresentation) superType));
        assertThat(iterator.hasNext(), is(false));
        assertThat(iterator.hasNext(), is(false));
    }

    @Test(expected = NoSuchElementException.class)
    public void testHasNotNext() throws Exception {
        Iterator<TypeRepresentation> iterator = new TypeRepresentation.TypeHierarchyIterator(typeDescription);
        assertThat(iterator.next(), is(typeDescription));
        assertThat(iterator.next(), is((TypeRepresentation) superType));
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNoRemoval() throws Exception {
        new TypeRepresentation.TypeHierarchyIterator(typeDescription).remove();
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(TypeRepresentation.TypeHierarchyIterator.class).applyBasic();
    }
}
