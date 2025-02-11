package net.bytebuddy.dynamic;

import net.bytebuddy.test.utility.ObjectPropertyAssertion;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ClassFileLocatorNoOpTest {

    private static final String FOO = "foo";

    @Test
    public void testLocation() throws Exception {
        assertThat(ClassFileLocator.NoOp.INSTANCE.locate(FOO).isResolved(), is(false));
    }

    @Test
    public void testObjectProperties() throws Exception {
        ObjectPropertyAssertion.of(ClassFileLocator.NoOp.class).apply();
    }
}
