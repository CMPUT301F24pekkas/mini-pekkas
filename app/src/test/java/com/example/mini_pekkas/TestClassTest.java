package com.example.mini_pekkas;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * A really basic unit test for TestClass. Delete whenever we have actual unit tests
 */
public class TestClassTest {
    private TestClass testClass = new TestClass(1);

    @Test
    public void testExecute() {
        assertEquals(2, testClass.addEpic());
    }
}
