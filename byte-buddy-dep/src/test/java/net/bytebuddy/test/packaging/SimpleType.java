package net.bytebuddy.test.packaging;

import net.bytebuddy.agent.builder.AgentBuilderDefaultApplicationTest;

@AgentBuilderDefaultApplicationTest.ShouldRebase
public class SimpleType {

    private static final String FOO = "foo";

    public String foo() {
        return FOO;
    }

}
